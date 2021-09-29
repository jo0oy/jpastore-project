package jpabook.jpastore.web.api;

import jpabook.jpastore.application.CategoryService;
import jpabook.jpastore.application.dto.category.CategoryResponseDto;
import jpabook.jpastore.dto.category.CategorySaveRequestDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.web.response.ResponseMessage;
import jpabook.jpastore.web.response.ResultResponse;
import jpabook.jpastore.web.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryApiController {

    private final CategoryService categoryService;

    @PostMapping("/api/v1/category")
    public ResponseEntity<ResultResponse<?>> createCategory(@RequestBody CategorySaveRequestDto requestDto) {

        return ResponseEntity.created(URI.create("/api/v1/category"))
                .body(ResultResponse.res(StatusCode.OK,
                        ResponseMessage.CREATED_CATEGORY, categoryService.createCategory(requestDto)));
    }

    @GetMapping("/api/v1/category/{id}")
    public ResponseEntity<ResultResponse<?>> getCategoryById(@PathVariable(name = "id") Long id) {
        CategoryResponseDto data = categoryService.getCategoryById(id);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY, data));
    }

    @GetMapping("/api/v2/category/items/{id}")
    public ResponseEntity<ResultResponse<?>> getCategoriesWithItems(@PathVariable(name = "id") Long id) {
        List<CategoryResponseDto> data = categoryService.getItemsByCategoryId_V2(id);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_ITEMS, data));
    }

    @GetMapping("/api/v1/category/items/{id}")
    public ResponseEntity<ResultResponse<?>> getItemsByCategory(@PathVariable(name = "id") Long id) {
        ItemListResponseDto data = categoryService.getItemsByCategoryId_V1(id);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_CATEGORY_ITEMS, data));
    }
}
