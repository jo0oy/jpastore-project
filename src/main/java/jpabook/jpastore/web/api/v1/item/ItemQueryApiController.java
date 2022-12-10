package jpabook.jpastore.web.api.v1.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.item.ItemService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.item.ItemDto;
import jpabook.jpastore.web.dto.item.ItemDtoMapper;
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
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "상품 조회 v1 API", description = "상품 조회 v1 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
@RestController
public class ItemQueryApiController {

    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;


    @Operation(summary = "상품 상세 정보 단건 조회", description = "상품의 상세 정보 조회 요청입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@Parameter(name = "id", description = "조회할 상품 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id) {

        var data = itemService.itemDetail_V1(id);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ITEM, data));
    }

    @Operation(summary = "상품 리스트 조회 by 상품명: Query Method 사용", description = "대/소문자 상관없이 검색어(상품명)와 같거나 검색어를 포함하는 상품 리스트 조회 요청입니다.")
    @GetMapping("/list/search")
    public ResponseEntity<?> searchItemsByName(@Parameter(name = "name", description = "검색할 상품명", in = ParameterIn.QUERY, required = true)
                                                   @RequestParam(name = "name") String name) {

        var items = itemService.searchItemsByName_V1(name)
                .stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new ItemDto.ListResponse<>(items);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data));
    }


    @Operation(summary = "전체 상품 리스트 조회", description = "전체 상품 리스트 조회 요청입니다.")
    @GetMapping("/list")
    public ResponseEntity<?> itemList() {

        var items = itemService.itemList()
                .stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new ItemDto.ListResponse<>(items);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data));
    }


    @Operation(summary = "전체 상품 리스트 조회 (페이징/정렬/검색 기능 포함)", description = "페이징, 정렬, 검색(상품명/최소 금액/최대 금액) 기능을 포함한 상품 리스트 조회 요청입니다.")
    @GetMapping("")
    public ResponseEntity<?> searchItemsPaging(@Parameter(name = "name", description = "검색할 상품명", in = ParameterIn.QUERY) @RequestParam(name = "name", required = false) String name,
                                               @Parameter(name = "minPrice", description = "상품 최소 금액(상품 가격 >= 최소 금액)", in = ParameterIn.QUERY) @RequestParam(name = "minPrice", required = false) Integer minPrice,
                                               @Parameter(name = "maxPrice", description = "상품 최대 금액(상품 가격 <= 최대 금액)", in = ParameterIn.QUERY) @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
                                               @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        var condition = ItemDto.SearchCondition.builder()
                .name(name).minPrice(minPrice).maxPrice(maxPrice).build();

        var data = itemService.items(itemDtoMapper.toCommand(condition), pageable)
                .map(itemDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ITEMS, data));
    }
}
