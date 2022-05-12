package jpabook.jpastore.domain.order.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderSearchCondition;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.queryRepo.OrderItemQueryDto;
import jpabook.jpastore.domain.order.queryRepo.OrderQueryDto;
import jpabook.jpastore.domain.order.queryRepo.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static jpabook.jpastore.domain.item.QItem.item;
import static jpabook.jpastore.domain.member.QMember.member;
import static jpabook.jpastore.domain.order.QDelivery.delivery;
import static jpabook.jpastore.domain.order.QOrder.order;
import static jpabook.jpastore.domain.order.QOrderItem.orderItem;

// 모든 주문 리스트 조회 default order => 최신순 (order id desc)
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // 일대일 매핑의 delivery, member 와 함께 단일 주문건 조회 (주문 id)
    @Override
    public Order findOrderWithMemberDelivery(Long orderId) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .join(order.member, member).fetchJoin()
                .where(order.id.eq(orderId))
                .fetchOne();
    }

    @Override
    public Order findOrderWithDelivery(Long orderId) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.id.eq(orderId))
                .fetchOne();
    }

    @Override
    public Long findDeliveryId(Long orderId) {
        return jpaQueryFactory
                .select(delivery.id)
                .from(order)
                .join(order.delivery, delivery)
                .where(order.id.eq(orderId))
                .fetchOne();
    }

    // 일대일 매핑의 delivery, member 와 함께 모든 주문 리스트 조회 (최신순) : order id desc
    // 1. list 조회
    @Override
    public List<Order> findAllWithMemberDelivery() {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .orderBy(order.id.desc())
                .fetch();
    }

    // 2. page 조회
    @Override
    public Page<Order> findAllWithMemberDelivery(Pageable pageable) {
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(order.id.desc())
                .fetch();

        JPAQuery<Order> countQuery = jpaQueryFactory.selectFrom(order);

        return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchCount);
    }

    // order search 여러 조건으로 동적쿼리 조회
    // 1. list 조회
    @Override
    public List<Order> findByCondition(OrderSearchCondition condition) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .where(memberIdEq(condition.getMemberId()),
                        orderIdEq(condition.getOrderId()),
                        memberNameEq(condition.getMemberName()),
                        orderStatusEq(condition.getStatus()),
                        deliveryStatusEq(condition.getDeliveryStatus()))
                .orderBy(order.id.desc())
                .fetch();
    }

    // 2. page 조회
    @Override
    public Page<Order> findByCondition(OrderSearchCondition condition, Pageable pageable) {
        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .where(memberIdEq(condition.getMemberId()),
                        orderIdEq(condition.getOrderId()),
                        memberNameEq(condition.getMemberName()),
                        orderStatusEq(condition.getStatus()),
                        deliveryStatusEq(condition.getDeliveryStatus()))
                .orderBy(order.id.desc())
                .fetch();

        JPAQuery<Order> countQuery = jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.member, member)
                .leftJoin(order.delivery, delivery)
                .where(memberIdEq(condition.getMemberId()),
                        orderIdEq(condition.getOrderId()),
                        memberNameEq(condition.getMemberName()),
                        orderStatusEq(condition.getStatus()),
                        deliveryStatusEq(condition.getDeliveryStatus()));

        return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchCount);
    }

    // OrderSimpleDto 주문 리스트 조회
    @Override
    public List<OrderSimpleQueryDto> findAllOrderSimpleDto() {
        return jpaQueryFactory
                .select(Projections.fields(OrderSimpleQueryDto.class,
                    order.id.as("orderId"),
                    order.member.name.as("name"),
                    order.createdDate.as("orderDateTime"),
                    order.status.as("orderStatus"),
                    order.delivery.address
                )).from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .orderBy(order.id.desc())
                .fetch();
    }

    // OrderQueryDto 조회 -> 1. orderIds IN 쿼리로 orderItemDtoMap 생성 / 2. OrderDto 로 조회한 결과에 OrderItemDto set
    @Override
    public List<OrderQueryDto> findAllOrderDto() {
        List<OrderQueryDto> result = orderDtoListWithoutOrderItems();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = getOrderItemMap(findOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> getOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = jpaQueryFactory
                .select(Projections.constructor(OrderItemQueryDto.class,
                        orderItem.order.id.as("orderId"),
                        orderItem.item.name.as("itemName"),
                        orderItem.orderPrice.as("orderPrice"),
                        orderItem.quantity
                ))
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.in(orderIds))
                .fetch();

        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    private List<Long> findOrderIds(List<OrderQueryDto> list) {
        return list.stream().map(OrderQueryDto::getOrderId).collect(Collectors.toList());
    }

    private List<OrderQueryDto> orderDtoListWithoutOrderItems() {
        return jpaQueryFactory
                .select(Projections.bean(OrderQueryDto.class,
                        order.id.as("orderId"),
                        order.member.name.as("name"),
                        order.createdDate.as("orderDateTime"),
                        order.status.as("orderStatus"),
                        delivery.status.as("deliveryStatus"),
                        delivery.address
                ))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .orderBy(order.id.desc())
                .fetch();
    }


    private BooleanExpression deliveryStatusEq(DeliveryStatus deliveryStatus) {
        return Objects.nonNull(deliveryStatus) ? order.delivery.status.eq(deliveryStatus) : null;
    }

    private BooleanExpression orderStatusEq(OrderStatus status) {
        return Objects.nonNull(status) ? order.status.eq(status) : null;
    }

    private BooleanExpression memberNameEq(String memberName) {
        return StringUtils.hasText(memberName) ? order.member.name.eq(memberName) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return Objects.nonNull(memberId) ? order.member.id.eq(memberId) : null;
    }

    private BooleanExpression orderIdEq(Long orderId) {
        return Objects.nonNull(orderId) ? order.id.eq(orderId) : null;
    }

}
