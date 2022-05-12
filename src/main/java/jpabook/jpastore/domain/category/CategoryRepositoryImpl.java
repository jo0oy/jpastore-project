package jpabook.jpastore.domain.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.domain.item.QItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static jpabook.jpastore.domain.category.QCategory.*;
import static jpabook.jpastore.domain.category.QCategoryItem.categoryItem;
import static jpabook.jpastore.domain.item.QItem.*;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Category> findByIdFetchJoin(Long id) {
        return Optional.ofNullable(
                queryFactory
                .selectFrom(category)
                .join(category.parent).fetchJoin()
                .where(category.id.eq(id))
                .fetchOne());
    }

    @Override
    public Optional<Category> findByNameFetchJoin(String name) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(category)
                        .join(category.parent).fetchJoin()
                        .where(category.name.eq(name))
                        .fetchOne());
    }

    @Override
    public Optional<Category> findParentByCategoryId(Long id) {
        return Optional.ofNullable(queryFactory
                .select(category.parent)
                .from(category)
                .innerJoin(category.parent)
                .where(category.id.eq(id))
                .fetchOne());
    }

    @Override
    public List<Category> findAllFetchJoin() {
        return queryFactory
                .selectFrom(category)
                .leftJoin(category.parent).fetchJoin()
                .fetch();
    }

    @Override
    public Page<Category> findAllFetchJoin(Pageable pageable) {
        return null;
    }

    @Override
    public List<Category> findAllRootParents() {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.isNull())
                .fetch();
    }

    @Override
    public List<Long> findChildIdsByParentId(Long id) {
        return queryFactory
                .select(category.id)
                .from(category)
                .where(category.parent.id.eq(id))
                .fetch();
    }

    @Override
    public List<Item> findItemsByCategoryId(Long id) {
        return queryFactory
                .select(item)
                .from(categoryItem)
                .where(categoryItem.category.id.eq(id))
                .fetch();
    }

    @Override
    public void deleteCategoryItemByItemId(Long id) {
        queryFactory
                .delete(categoryItem)
                .where(categoryItem.item.id.eq(id));
    }

    @Override
    public long totalCount() {
        return queryFactory
                .select(category.id)
                .from(category)
                .fetchCount();
    }
}
