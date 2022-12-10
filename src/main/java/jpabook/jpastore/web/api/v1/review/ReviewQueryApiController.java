package jpabook.jpastore.web.api.v1.review;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.review.ReviewService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.review.ReviewDto;
import jpabook.jpastore.web.dto.review.ReviewDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "리뷰 조회 API", description = "리뷰 조회 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewQueryApiController {

    private final ReviewService reviewService;
    private final ReviewDtoMapper reviewDtoMapper;

    @Operation(summary = "리뷰 상세 정보 단건 조회", description = "단일 리뷰의 상세 정보 조회 요청입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewInfo(@Parameter(name = "id", description = "조회할 리뷰 id", in = ParameterIn.PATH, required = true) @PathVariable("id") Long id) {
        var review = reviewService.getReviewInfo(id);
        var data = reviewDtoMapper.toDto(review);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_REVIEW, data));
    }

    @Operation(summary = "전체 리뷰 리스트 조회(검색 기능)", description = "검색 기능(회원 id/회원 로그인ID/상품 id)이 포함된 전체 리뷰 리스트 조회 요청입니다.")
    @GetMapping("/list")
    public ResponseEntity<?> reviewList(@Parameter(name = "memberId", description = "작성자 회원 id", in = ParameterIn.QUERY) @RequestParam(name = "memberId", required = false) Long memberId,
                                        @Parameter(name = "username", description = "작성자 회원 로그인ID", in = ParameterIn.QUERY) @RequestParam(name = "username", required = false) String username,
                                        @Parameter(name = "itemId", description = "리뷰 작성한 상품 id", in = ParameterIn.QUERY) @RequestParam(name = "itemId", required = false) Long itemId) {

        var condition = ReviewDto.SearchCondition.builder()
                .memberId(memberId).username(username).itemId(itemId).build();

        var list = reviewService.listReview(reviewDtoMapper.toCommand(condition))
                .stream()
                .map(reviewDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new ReviewDto.ListResponse<>(list);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_REVIEW, data));
    }

    @Operation(summary = "전체 리뷰 리스트 조회(페이징, 정렬, 검색 기능 포함)", description = "페이징, 정렬, 검색(회원 ID/회원 로그인ID/상품 id) 기능을 포함한 리뷰 리스트 조회 요청입니다")
    @GetMapping("")
    public ResponseEntity<?> reviewListPaging(@Parameter(name = "memberId", description = "작성자 회원 id", in = ParameterIn.QUERY) @RequestParam(name = "memberId", required = false) Long memberId,
                                              @Parameter(name = "username", description = "작성자 회원 로그인ID", in = ParameterIn.QUERY) @RequestParam(name = "username", required = false) String username,
                                              @Parameter(name = "itemId", description = "리뷰 작성한 상품 id", in = ParameterIn.QUERY) @RequestParam(name = "itemId", required = false) Long itemId,
                                              @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        var condition = ReviewDto.SearchCondition.builder()
                .memberId(memberId).username(username).itemId(itemId).build();

        var data
                = reviewService.listReview(reviewDtoMapper.toCommand(condition), pageable)
                .map(reviewDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_REVIEW, data));
    }
}
