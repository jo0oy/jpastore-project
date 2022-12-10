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
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!pageable.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
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

        return new OrderSpecifier<>(Order.DESC, item.createdDate); // 디폴트 정렬갑: 최신순 (createdDate, DESC)
    }
}
