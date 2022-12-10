package jpabook.jpastore.application.category;

import java.util.List;

public interface CategoryService {

    Long registerCategory(CategoryCommand.RegisterReq command);

    // 1. 아이디로 조회
    // v1. dto 변환 과정에서 불필요한 parent category 까지 in 쿼리에 포함.
    CategoryInfo.DetailWithItemsInfo getCategoryWithItems(Long id);

    // v2. 컬렉션 매핑관계에 접근하는 순서 변동한 CategoryInfo.MainInfo 로 변환(v1 문제 해결)
    // -> parent 를 가장 마지막에 접근
    CategoryInfo.MainInfo getCategory(Long id);

    // v3. parent fetch join 으로 가져올 경우 v1과 동일한 문제 발생.
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
