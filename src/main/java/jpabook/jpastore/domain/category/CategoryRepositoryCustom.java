package jpabook.jpastore.domain.category;

import jpabook.jpastore.domain.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryCustom {

    boolean existsByName(String name);

    Optional<Category> findCategoryById(Long categoryId);

    Optional<Category> findCategoryByIdWithParent(Long id);

    Optional<Category> findCategoryByNameWithParent(String name);

    Optional<Category> findParentByCategoryId(Long id);

    List<Category> findAllWithParents();

    Page<Category> findAllWithParents(Pageable pageable);

    List<Category> findAllRootParents();

    List<Long> findChildIdsByParentId(Long id);

    List<Long> findCategoryIdsInChildIdsAndEqName(Long parentId, String name);

    List<Item> findItemsByCategoryId(Long id);

    long totalCount();
}
