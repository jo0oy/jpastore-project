package jpabook.jpastore.web.api.v2.item;

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
@Tag(name = "상품 조회 v2 API", description = "상품 조회 v2 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v2/items")
@RestController
public class ItemQueryApiControllerV2 {

    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;

    @Operation(summary = "상품 상세 정보 단건 조회: @JsonIgnore 활용해 필요한 정보만 반환", description = "상품의 상세 정보 조회 요청입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@Parameter(name = "id", description = "조회할 상품 id", in = ParameterIn.PATH, required = true)
                                         @PathVariable(name = "id") Long id) {

        var item = itemService.itemDetail_V2(id);
        var data = itemDtoMapper.toDto(item);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ITEM, data));
    }

    @Operation(summary = "상품 리스트 조회 by 상품명: JPQL 작성", description = "대/소문자 상관없이 검색어(상품명)와 같거나 검색어를 포함하는 상품 리스트 조회 요청입니다.")
    @GetMapping("/list/search")
    public ResponseEntity<?> searchItemsByName(@Parameter(name = "name", description = "검색할 상품명", in = ParameterIn.QUERY, required = true) @RequestParam(name = "name") String name) {
        var items = itemService.searchItemsByName_V2(name).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new ItemDto.ListResponse<>(items);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ITEMS, data));
    }
}
