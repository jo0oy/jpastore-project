package jpabook.jpastore.domain.order.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.common.exception.BadRequestException;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderSearchCondition;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static jpabook.jpastore.domain.item.QItem.item;
import static jpabook.jpastore.domain.member.QMember.member;
import static jpabook.jpastore.domain.order.QDelivery.delivery;
import static jpabook.jpastore.domain.order.QOrder.order;
import static jpabook.jpastore.domain.order.QOrderItem.orderItem;


// 모든 주문 리스트 조회 default order => 최신순 (order id desc)
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 일대일 매핑의 delivery, member 와 함께 단일 주문건 조회 (주문 id)
    @Override
    public Optional<Order> findOrderWithMemberDelivery(Long orderId) {
        return Optional.ofNullable(
                queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .join(order.member, member).fetchJoin()
                .where(eqOrderId(orderId))
                .fetchOne()
        );
    }

    @Override
    public Optional<Order> findOrderWithMember(Long orderId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(order)
                        .join(order.member, member).fetchJoin()
                        .where(eqOrderId(orderId))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Order> findOrderWithDelivery(Long orderId) {
        return Optional.ofNullable(
                queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(eqOrderId(orderId))
                .fetchOne()
        );
    }

    @Override
    public Long findDeliveryId(Long orderId) {
        return queryFactory
                .select(delivery.id)
                .from(order)
                .join(order.delivery, delivery)
                .where(eqOrderId(orderId))
                .fetchOne();
    }

    // 일대일 매핑의 delivery, member 와 함께 모든 주문 리스트 조회 (최신순) : order id desc
    // 1. list 조회
    @Override
    public List<Order> findAllWithMemberDelivery() {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .orderBy(order.createdDate.desc())
                .fetch();
    }

    // 2. page 조회
    @Override
    public Page<Order> findAllWithMemberDelivery(Pageable pageable) {
        var orders = queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery = queryFactory.selectFrom(order)
                .join(order.member, member)
                .join(order.delivery, delivery);

        return PageableExecutionUtils.getPage(orders, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<Order> findAllWithOrderItems() {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .fetch();
    }

    @Override
    public List<Order> findAllWithOrderItemsDistinct() {
        return queryFactory
                .selectFrom(order)
                .distinct()
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .fetch();
    }

    // order search 여러 조건으로 동적쿼리 조회
    // 1. list 조회
    @Override
    public List<Order> findByCondition(OrderSearchCondition condition) {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .where(eqMemberId(condition.getMemberId()),
                        eqOrderId(condition.getOrderId()),
                        containsMemberName(condition.getMemberName()),
                        eqOrderStatus(condition.getStatus()),
                        eqDeliveryStatus(condition.getDeliveryStatus()))
                .orderBy(order.createdDate.desc())
                .fetch();
    }

    // 2. page 조회
    @Override
    public Page<Order> findByCondition(OrderSearchCondition condition, Pageable pageable) {
        var orders = queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .where(eqMemberId(condition.getMemberId()),
                        eqOrderId(condition.getOrderId()),
                        containsMemberName(condition.getMemberName()),
                        eqOrderStatus(condition.getStatus()),
                        eqDeliveryStatus(condition.getDeliveryStatus()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(getSort(pageable))
                .fetch();

        var countQuery =
                queryFactory.selectFrom(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .where(eqMemberId(condition.getMemberId()),
                        eqOrderId(condition.getOrderId()),
                        containsMemberName(condition.getMemberName()),
                        eqOrderStatus(condition.getStatus()),
                        eqDeliveryStatus(condition.getDeliveryStatus())
                );

        return PageableExecutionUtils.getPage(orders, pageable, () -> countQuery.fetch().size());
    }

    // Projections 조회
    // 1. OrderQueryInfo.SimpleInfo 주문 리스트 조회
    @Override
    public List<OrderQueryInfo.SimpleInfo> findAllOrderSimpleInfo() {
        return queryFactory
                .select(Projections.constructor(OrderQueryInfo.SimpleInfo.class,
                    order.id.as("orderId"),
                    order.member.username.as("memberName"),
                    order.createdDate.as("orderedDate"),
                    order.status.as("orderStatus"),
                    order.delivery.status.as("deliveryStatus"),
                    order.delivery.address
                )).from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .orderBy(order.createdDate.desc())
                .fetch();
    }

    // OrderQueryDto 조회 -> 1. orderIds IN 쿼리로 orderItemDtoMap 생성 / 2. OrderDto 로 조회한 결과에 OrderItemDto set
    @Override
    public List<OrderQueryInfo.MainInfo> findAllOrderInfo() {

        // 1. orderItem 정보 제외한 MainInfo 리스트 조회
        var result = orderInfoListWithoutOrderItems();

        // 2. IN 쿼리 활용해 orderId별 orderItem 리스트 맵 생성
        var orderItemMap = getOrderItemMap(getOrderIds(result));

        // 3. MainInfo 에 해당 orderItem 리스트 매핑
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /**
     *
     * @param orderIds
     * @return [orderId - OrderItemInfo 리스트] map
     */
    private Map<Long, List<OrderQueryInfo.OrderItemInfo>> getOrderItemMap(List<Long> orderIds) {
        var orderItems = queryFactory
                .select(Projections.constructor(OrderQueryInfo.OrderItemInfo.class,
                        orderItem.order.id.as("orderId"),
                        orderItem.item.name.as("itemName"),
                        orderItem.orderPrice.as("orderPrice"),
                        orderItem.quantity
                ))
                .from(orderItem)
                .join(orderItem.order, order)
                .join(orderItem.item, item)
                .where(order.id.in(orderIds))
                .fetch();

        return orderItems.stream().collect(Collectors.groupingBy(OrderQueryInfo.OrderItemInfo::getOrderId));
    }

    /**
     * orderId 리스트 조회
     */
    private List<Long> getOrderIds(List<OrderQueryInfo.MainInfo> list) {
        return list.stream().map(OrderQueryInfo.MainInfo::getOrderId).collect(Collectors.toList());
    }

    /**
     * orderItem list 제외한 MainInfo 조회
     */
    private List<OrderQueryInfo.MainInfo> orderInfoListWithoutOrderItems() {
        return queryFactory
                .select(Projections.constructor(OrderQueryInfo.MainInfo.class,
                        order.id.as("orderId"),
                        order.member.username.as("memberName"),
                        order.createdDate.as("orderedDate"),
                        order.status.as("orderStatus"),
                        delivery.status.as("deliveryStatus"),
                        delivery.address
                ))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .orderBy(order.createdDate.desc())
                .fetch();
    }


    private BooleanExpression eqDeliveryStatus(DeliveryStatus deliveryStatus) {
        return Objects.nonNull(deliveryStatus) ? delivery.status.eq(deliveryStatus) : null;
    }

    private BooleanExpression eqOrderStatus(OrderStatus status) {
        return Objects.nonNull(status) ? order.status.eq(status) : null;
    }

    private BooleanExpression containsMemberName(String memberName) {
        return StringUtils.hasText(memberName) ? member.username.containsIgnoreCase(memberName) : null;
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return Objects.nonNull(memberId) ? member.id.eq(memberId) : null;
    }

    private BooleanExpression eqOrderId(Long orderId) {
        return Objects.nonNull(orderId) ? order.id.eq(orderId) : null;
    }

    private OrderSpecifier<?> getSort(Pageable pageable) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        String property = null;
        if (!pageable.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order sortOrder : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                com.querydsl.core.types.Order direction = sortOrder.getDirection().isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                property = sortOrder.getProperty();
                switch (property) {
                    case "id":
                        return new OrderSpecifier<>(direction, order.id);
                    case "order.memberId":
                        return new OrderSpecifier<>(direction, order.member.id);
                    case "order.username":
                        return new OrderSpecifier<>(direction, order.member.username);
                    case "createdDate":
                        return new OrderSpecifier<>(direction, order.createdDate);
                }
            }
        }

        throw new BadRequestException("올바르지 않은 정렬 기준 속성입니다. property=" + property);
    }
}
