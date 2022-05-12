package jpabook.jpastore.application.category;

import jpabook.jpastore.application.dto.category.CategoryListResponseDto;
import jpabook.jpastore.application.dto.category.CategoryParentChildDto;
import jpabook.jpastore.application.dto.category.CategoryResponseDto;
import jpabook.jpastore.application.dto.category.CategorySingleResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.dto.category.CategorySaveRequestDto;
import jpabook.jpastore.dto.category.CategoryUpdateReqDto;

import java.util.List;

public interface CategoryService {

    Long create(CategorySaveRequestDto requestDto);

    // 1. 아이디로 조회
    // v1. dto 변환 과정에서 불필요한 parent category 까지 in 쿼리에 포함.
    CategoryResponseDto getCategory(Long id);

    // v2. 컬렉션 매핑관계에 접근하는 순서 변동한 CategorySingleResponseDto 로 변환(v1 문제 해결)
    // -> parent 를 가장 마지막에 접근
    CategorySingleResponseDto getCategoryV2(Long id);

    // v3. parent fetch join 으로 가져올 경우 v1과 동일한 문제 발생.
    CategorySingleResponseDto getCategoryV3(Long id);

    // parent id is null 인 카테고리 조회 -> dto 로 넘기기
    // dto 에서 getChild()를 통해 in 쿼리로 child 가져옴. (카테고리 깊이 + 1번 만큼 in 쿼리 발생)
    CategoryListResponseDto<CategoryParentChildDto> categoryList();

    List<CategoryResponseDto> list();

    ItemListResponseDto<ItemResponseDto> itemListByCategoryId(Long categoryId);

    // v2 : 카테고리 상품 리스트 조회 + 해당 카테고리 상세 정보 포함
    CategoryResponseDto categoryWithItems(Long categoryId);

    void update(Long categoryId, CategoryUpdateReqDto requestDto);

    void delete(Long id);
}
