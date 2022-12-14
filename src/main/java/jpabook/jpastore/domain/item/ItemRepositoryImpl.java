package jpabook.jpastore.domain.item;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

import static jpabook.jpastore.domain.item.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(item)
                        .where(eqItemId(itemId), notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Page<Item> findAll(ItemSearchCondition condition, Pageable pageable) {
        var content
                =  queryFactory.selectFrom(item)
                .where(containsName(condition.getName()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        notDeleted())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery = queryFactory.selectFrom(item)
                .where(containsName(condition.getName()),
                        goeMinPrice(condition.getMinPrice()),
                        loeMaxPrice(condition.getMaxPrice()),
                        notDeleted());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression notDeleted() {
        return item.isDeleted.eq(false);
    }

    private BooleanExpression eqItemId(Long itemId) {
        return Objects.nonNull(itemId) ? item.id.eq(itemId) : null;
    }

    private BooleanExpression containsName(String name) {
        return (StringUtils.hasText(name)) ? item.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression goeMinPrice(Integer minPrice) {
        return (minPrice != null && minPrice >= 0) ? item.price.value.goe(minPrice) : null;
    }

    private BooleanExpression loeMaxPrice(Integer maxPrice) {
        return (maxPrice != null && maxPrice >= 0) ? item.price.value.loe(maxPrice) : null;
    }

    private OrderSpecifier<?> getSort(Pageable pageable) {
        //??????????????? ????????? Pageable ????????? ???????????? null ??? ??????
        if (!pageable.getSort().isEmpty()) {
            //???????????? ?????? ????????? for ???????????? ?????? ????????????
            for (Sort.Order order : pageable.getSort()) {
                // ??????????????? ????????? DESC or ASC ??? ????????????.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // ??????????????? ????????? ?????? ????????? ????????? ????????? ?????? ???????????? ???????????? ??????.
                switch (order.getProperty()) {
                    case "id":
                        return new OrderSpecifier<>(direction, item.id);
                    case "name":
                        return new OrderSpecifier<>(direction, item.name);
                    case "price":
                        return new OrderSpecifier<>(direction, item.price.value);
                    case "createdDate":
                        return new OrderSpecifier<>(direction, item.createdDate);
                }
            }
        }

        return new OrderSpecifier<>(Order.DESC, item.createdDate); // ????????? ?????????: ????????? (createdDate, DESC)
    }
}
