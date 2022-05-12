package jpabook.jpastore.domain.category;

import jpabook.jpastore.domain.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryCustom {

    Optional<Category> findByIdFetchJoin(Long id);

    Optional<Category> findByNameFetchJoin(String name);

    Optional<Category> findParentByCategoryId(Long id);

    List<Category> findAllFetchJoin();

    Page<Category> findAllFetchJoin(Pageable pageable);

    List<Category> findAllRootParents();

    List<Long> findChildIdsByParentId(Long id);

    List<Item> findItemsByCategoryId(Long id);

    void deleteCategoryItemByItemId(Long id);

    long totalCount();
}
