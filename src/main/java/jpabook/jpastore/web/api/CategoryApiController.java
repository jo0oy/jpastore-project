package jpabook.jpastore.web.api;

import jpabook.jpastore.application.category.CategoryServiceImpl;
import jpabook.jpastore.dto.category.CategorySaveRequestDto;
import jpabook.jpastore.dto.category.CategoryUpdateReqDto;
import jpabook.jpastore.web.response.ResponseMessage;
import jpabook.jpastore.web.response.ResultResponse;
import jpabook.jpastore.web.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class CategoryApiController {

    private final CategoryServiceImpl categoryService;

    /**
     * 카테고리 생성
     */
    @PostMapping("/api/v1/category")
    public ResponseEntity<ResultResponse<?>> createCategory(@RequestBody CategorySaveRequestDto requestDto) {

        return ResponseEntity.created(URI.create("/api/v1/category"))
                .body(ResultResponse.res(StatusCode.OK,
                        ResponseMessage.CREATED_CATEGORY, categoryService.create(requestDto)));
    }

    /**
     * 카테고리 단일 조회
     */
    // 간단 조회 (상품 리스트 미포함)
    @GetMapping("/api/v1/simple-category/{id}")
    public ResponseEntity<ResultResponse<?>> categoryById(@PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY, categoryService.getCategoryV2(id)));
    }

    // 상세 조회
    @GetMapping("/api/v1/category/{id}")
    public ResponseEntity<ResultResponse<?>> categoryDetailById(@PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY, categoryService.categoryWithItems(id)));
    }

    /**
     * 전체 카테고리 리스트 조회
     */
    @GetMapping("/api/v1/category/list")
    public ResponseEntity<ResultResponse<?>> categoryList() {

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_LIST, categoryService.categoryList()));
    }

    /**
     * 카테고리-상품 리스트 조회 (by category id)
     */
    @GetMapping("/api/v1/category/{id}/items")
    public ResponseEntity<ResultResponse<?>> getItemsByCategory(@PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_ITEMS, categoryService.itemListByCategoryId(id)));
    }

    /**
     * 카테고리 수정
     */
    @PostMapping("/api/v1/category/{id}")
    public ResponseEntity<ResultResponse<?>> update(@PathVariable(name = "id") Long id,
                                                    @RequestBody CategoryUpdateReqDto requestDto) {
        categoryService.update(id, requestDto);
        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.UPDATED_CATEGORY));
    }
}
