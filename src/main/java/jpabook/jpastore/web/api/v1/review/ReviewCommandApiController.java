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
import jpabook.jpastore.domain.auth.AuthMember;
import jpabook.jpastore.web.dto.review.ReviewDto;
import jpabook.jpastore.web.dto.review.ReviewDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "리뷰 등록/수정/삭제 API", description = "리뷰 등록/수정/삭제 API 입니다. ** 인증된 회원만 접근 가능합니다! **")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')") // 인증된 사용자만 접근 가능합니다.
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewCommandApiController {

    private final ReviewService reviewService;
    private final ReviewDtoMapper reviewDtoMapper;

    @Operation(summary = "리뷰 등록", description = "리뷰 등록 요청입니다. ** 인증된 회원만 요청 가능합니다! ** ")
    @PostMapping("")
    public ResponseEntity<?> registerReview(@Valid @RequestBody ReviewDto.RegisterReviewReq request) {

        var registeredId = reviewService.registerReview(reviewDtoMapper.toCommand(request));

        var data = reviewDtoMapper.toDto(registeredId);

        return ResponseEntity.created(URI.create("/api/v1/reviews"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_REVIEW, data));
    }

    @Operation(summary = "리뷰 수정", description = "리뷰 수정 요청입니다. ** 인증된 작성자 '본인'만 요청 가능합니다! ** ")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@Parameter(name = "id", description = "수정할 리뷰 id", in = ParameterIn.PATH, required = true) @PathVariable("id") Long id,
                                          @Valid @RequestBody ReviewDto.UpdateReviewReq request,
                                          @AuthenticationPrincipal AuthMember authMember) {

        reviewService.updateReview(id, reviewDtoMapper.toCommand(request), authMember.getUsername());

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_REVIEW));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제 요청입니다. ** 인증된 작성자 '본인' 또는 관리자 권한일 경우 요청 가능합니다! ** ")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@Parameter(name = "id", description = "삭제할 리뷰 id", in = ParameterIn.PATH, required = true) @PathVariable("id") Long id,
                                          @AuthenticationPrincipal AuthMember authMember) {

        reviewService.deleteReview(id, authMember.getUsername());

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.DELETE_REVIEW));
    }
}
