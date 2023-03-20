package jpabook.jpastore.application.category;

import java.util.List;

public interface CategoryService {

    Long registerCategory(CategoryCommand.RegisterReq command);

    // 단일 카테고리 상세 조회 with 카테고리 상품 리스트
    CategoryInfo.DetailWithItemsInfo getCategoryWithItems(Long id);

    // 단일 카테고리 조회 by id
    CategoryInfo.MainInfo getCategory(Long id);

    // 단일 카테고리 조회 by id, fetch join parent
    CategoryInfo.MainInfo getCategoryV2(Long id);

    // 카테고리 계층 관계 리스트 조회
    // parent id is null 인 카테고리 조회 -> dto 로 넘기기
    // dto 에서 getChild()를 통해 in 쿼리로 child 가져옴. (카테고리 깊이 + 1번 만큼 in 쿼리 발생)
    List<CategoryInfo.ParentChildInfo> categoryHierarchicalList();

    List<CategoryInfo.MainInfo> categoryList();

    List<CategoryInfo.DetailWithItemsInfo> categoryDetailList();

    CategoryInfo.CategoryItemListInfo categoryItemListByCategoryId(Long categoryId);

    void update(Long categoryId, CategoryCommand.UpdateInfoReq command);

    void delete(Long id);
}
