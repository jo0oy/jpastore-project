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
import jpabook.jpastore.web.dto.category.CategoryDto;
import jpabook.jpastore.web.dto.category.CategoryDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "카테고리 등록/수정 API", description = "카테고리 등록/수정 요청 API 입니다.  ** '관리자' 권한만 요청 가능합니다! **")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')") // 카테고리 수정/삭제는 관리자만 접근 가능
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryCommandApiController {

    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;


    @Operation(summary = "카테고리 등록", description = "카테고리 등록 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PostMapping("")
    public ResponseEntity<?> registerCategory(@Valid @RequestBody CategoryDto.RegisterReq request) {
        var registeredCategoryId = categoryService.registerCategory(categoryDtoMapper.toCommand(request));

        var data = categoryDtoMapper.toDto(registeredCategoryId);

        return ResponseEntity.created(URI.create("/api/v1/category"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_CATEGORY, data));
    }


    @Operation(summary = "카테고리 수정", description = "카테고리 정보 수정 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PostMapping("/{id}")
    public ResponseEntity<?> updateCategoryInfo(@Parameter(name = "id", description = "수정할 카테고리 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id,
                                                @Valid @RequestBody CategoryDto.UpdateInfoReq request) {

        categoryService.update(id, categoryDtoMapper.toCommand(request));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_CATEGORY));
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제 요청입니다. 관리자 권한만 접근 가능합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@Parameter(name = "id", description = "삭제할 카테고리 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id) {

        categoryService.delete(id);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.DELETE_CATEGORY));
    }
}
