package jpabook.jpastore.domain.category;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static jpabook.jpastore.domain.category.QCategory.category;
import static jpabook.jpastore.domain.category.QCategoryItem.categoryItem;
import static jpabook.jpastore.domain.item.QItem.item;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByName(String name) {
        var exist =
                queryFactory.selectOne()
                        .from(category)
                        .where(eqName(name),
                                notDeleted())
                        .fetchOne();

        return exist != null;
    }

    @Override
    public Optional<Category> findCategoryById(Long categoryId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(category)
                        .where(eqCategoryId(categoryId), notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Optional<Category> findCategoryByIdWithParent(Long id) {
        return Optional.ofNullable(
                queryFactory
                .selectFrom(category)
                .join(category.parent).fetchJoin()
                .where(eqCategoryId(id),
                        notDeleted())
                .fetchOne()
        );
    }

    @Override
    public Optional<Category> findCategoryByNameWithParent(String name) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(category)
                        .join(category.parent).fetchJoin()
                        .where(eqName(name), notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Optional<Category> findParentByCategoryId(Long id) {
        return Optional.ofNullable(
                queryFactory
                .select(category.parent)
                .from(category)
                .innerJoin(category.parent)
                .where(eqCategoryId(id), notDeleted())
                .fetchOne()
        );
    }

    @Override
    public List<Category> findAllWithParents() {
        return queryFactory
                .selectFrom(category)
                .leftJoin(category.parent).fetchJoin()
                .where(notDeleted())
                .fetch();
    }

    @Override
    public Page<Category> findAllWithParents(Pageable pageable) {
        return null;
    }

    @Override
    public List<Category> findAllRootParents() {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.isNull(), notDeleted())
                .fetch();
    }

    @Override
    public List<Long> findChildIdsByParentId(Long id) {
        return queryFactory
                .select(category.id)
                .from(category)
                .leftJoin(category.parent)
                .where(category.parent.id.eq(id), notDeleted())
                .fetch();
    }

    @Override
    public List<Long> findCategoryIdsInChildIdsAndEqName(Long parentId, String name) {
        return queryFactory
                .select(category.id)
                .from(category)
                .where(category.id.in(
                        JPAExpressions.select(category.id)
                        .from(category)
                        .join(category.parent)
                        .where(category.parent.id.eq(parentId), notDeleted())),
                        eqName(name),
                        notDeleted()
                ).fetch();
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
    public long totalCount() {
        return queryFactory
                .select(category.id)
                .from(category)
                .where(notDeleted())
                .fetchCount();
    }

    private BooleanExpression notDeleted() {
        return category.isDeleted.eq(false);
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        return Objects.nonNull(categoryId) ? category.id.eq(categoryId) : null;
    }

    private BooleanExpression eqName(String name) {
        return Objects.nonNull(name) ? category.name.eq(name) : null;
    }
}
