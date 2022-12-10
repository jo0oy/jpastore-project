package jpabook.jpastore.web.api.v1.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.category.CategoryService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.category.CategoryDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "카테고리 조회 API", description = "카테고리 조회 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CategoryQueryApiController {

    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;

    @Operation(summary = "카테고리 간단 정보 단건 조회", description = "단일 카테고리의 간단한 정보 조회 요청입니다. (상품 리스트 미포함)")
    @GetMapping("/simple-categories/{id}")
    public ResponseEntity<?> categoryById(@Parameter(name = "id", description = "조회할 카테고리 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id) {

        var data = categoryDtoMapper.toDto(categoryService.getCategory(id));

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY, data));
    }


    @Operation(summary = "카테고리 상세 정보 단건 조회", description = "단일 카테고리의 상세 정보 조회 요청입니다. (카테고리의 상품 리스트 포함)")
    @GetMapping("/categories/{id}")
    public ResponseEntity<?> categoryDetailById(@Parameter(name = "id", description = "조회할 카테고리 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id) {

        var data = categoryDtoMapper.toDto(categoryService.getCategoryWithItems(id));

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY, data));
    }


    @Operation(summary = "전체 카테고리 리스트 조회", description = "전체 카테고리 리스트 조회 요청입니다.")
    @GetMapping("/categories/list")
    public ResponseEntity<?> categoryList() {

        var data = categoryService.categoryList().stream()
                .map(categoryDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_LIST, data));
    }


    @Operation(summary = "전체 카테고리 계층 트리 리스트 조회", description = "전체 카테고리 리스트의 부모-자식 관계를 계층 구조로 조회한 요청입니다.")
    @GetMapping("/categories/hierarchical-list")
    public ResponseEntity<?> categoryHierarchicalList() {

        var list = categoryService.categoryHierarchicalList();
        var data = list.stream()
                .map(categoryDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_LIST, data));
    }


    @Operation(summary = "카테고리 상품 리스트 조회 (by categoryId)", description = "id에 해당하는 카테고리의 전체 상품 리스트를 조회하는 요청입니다.")
    @GetMapping("/categories/{id}/items")
    public ResponseEntity<?> getItemsByCategory(@PathVariable(name = "id") Long id) {

        var data
                = categoryDtoMapper.toDto(categoryService.categoryItemListByCategoryId(id));

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_ITEMS, data));
    }
}
