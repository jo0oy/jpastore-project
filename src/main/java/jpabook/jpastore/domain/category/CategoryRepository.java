package jpabook.jpastore.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    @Query("select c from Category c where c.name = :name and c.isDeleted = false ")
    Optional<Category> findByName(@Param("name") String name);

    List<Category> findAllByName(String name);

    @Query("select ci from CategoryItem ci where ci.item.id = :itemId")
    List<CategoryItem> findCategoryItemsByItem_Id(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("delete from CategoryItem ci where ci.item.id = :itemId")
    void deleteCategoryItemsByItem_Id(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("delete from CategoryItem ci where ci.category.id = :categoryId")
    void deleteCategoryItemsByCategory_Id(@Param("categoryId") Long categoryId);
}
