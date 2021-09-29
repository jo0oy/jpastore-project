package jpabook.jpastore.domain.category.queryRepo;

import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CategoryQueryRepository {

    private final EntityManager em;

    public List<CategoryItem> findItemsByCategoryId(Long categoryId) {
        return em.createQuery("select ci from CategoryItem ci"
                + " join fetch ci.item i"
                + " where ci.category.id = :categoryId", CategoryItem.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public List<Category> findAllWithItems(Long categoryId) {
        return em.createQuery("select distinct c from Category c"
                + " join fetch c.categoryItems ci"
                + " join fetch ci.item i"
                + " where c.id = :categoryId", Category.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }
}
