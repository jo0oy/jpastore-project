package jpabook.jpastore.application;

import jpabook.jpastore.dto.order.OrderItemRequestDto;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import jpabook.jpastore.application.dto.order.OrderListResponseDto;
import jpabook.jpastore.application.dto.order.OrderResponseDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.order.*;
import jpabook.jpastore.domain.order.queryRepo.OrderQueryRepository;
import jpabook.jpastore.domain.order.queryRepo.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int quantity){
        // 엔티티 조회
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다. id = " + itemId)
        );
        // 배송정보 생성
        Delivery delivery = Delivery.builder()
                .address(member.getAddress())
                .status(DeliveryStatus.READY)
                .build();
        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), quantity);
        // 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        Order order = Order.createOrder(member, delivery, orderItems);

        return orderRepository.save(order).getId();
    }

    // OrderRequestDto 이용한 주문 생성
    @Transactional
    public Long orderByDto(OrderSaveRequestDto requestDto) {

        // 엔티티 조회
        Member member = memberRepository.findById(requestDto.getMemberId()).get();

        // 주문 상품 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto orderItemDto : requestDto.getOrderItems()) {
            Long itemId = orderItemDto.getItemId();
            int quantity = orderItemDto.getQuantity();
            // 상품 엔티티 조회
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 상품입니다. id = " + itemId)
            );

            orderItems.add(OrderItem.createOrderItem(item, item.getPrice(), quantity));
        }

        // 배송정보 생성
        Delivery delivery = Delivery.builder()
                .address(member.getAddress())
                .status(DeliveryStatus.READY)
                .build();

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItems);

        return orderRepository.save(order).getId();
    }

    /**
     * 단일 주문 조회
     */
    public OrderResponseDto findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다. id = " + orderId)
        );

        return new OrderResponseDto(order);
    }

    public OrderSimpleDto findOrderByIdWithMemberDelivery(Long orderId) {
        Order order = orderQueryRepository.findOneWithMemberDelivery(orderId);

        return new OrderSimpleDto(order);
    }

    /**
     * 전체 주문 조회
     */

    // 1. 엔티티를 DTO로 변환
    public OrderListResponseDto findAllOrders(){
        List<Order> orders = orderRepository.findAll();

        return new OrderListResponseDto(orders);
    }

    // 2.ToOne 관계 (Member, Delivery) 페치조인을 사용한 간단 조회
    public List<OrderSimpleDto> findAllWithMemberDelivery(){
        return orderQueryRepository.findAllWithMemberDelivery().stream()
                .map(OrderSimpleDto::new)
                .collect(Collectors.toList());
    }

    // 3. JPA에서 DTO를 직접 조회한 간단 조회
    public OrderListResponseDto<?> findOrderDtos() {
        List<OrderSimpleQueryDto> orders = orderQueryRepository.findOrderDtos();

        return new OrderListResponseDto<>(orders);
    }

    // 4. ToMany (OrderItems, Items)에 페치조인을 사용한 전체 조회
    public OrderListResponseDto<?> findAllWithItems() {
        List<Order> orders = orderQueryRepository.findAllWithItems();
        return new OrderListResponseDto<>(orders);
    }

    // 5. distinct 키워드를 사용한 컬렉션 페치 조인
    public OrderListResponseDto findAllWithItemsDistinct() {
        List<Order> orders = orderQueryRepository.findAllWithItemsDistinct();
        return new OrderListResponseDto(orders);
    }

    public OrderListResponseDto findAllWithItemsPaging(int offset, int limit) {
        List<Order> orders = orderQueryRepository.findAllWithMemberDelivery(offset, limit);

        return new OrderListResponseDto(orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList()));
    }

    public OrderListResponseDto findAllByDto() {
        return new OrderListResponseDto(orderQueryRepository.findOrderQueryDto());
    }

    public OrderListResponseDto findAllByDto_optimazation() {
        return new OrderListResponseDto(orderQueryRepository.findOrderQueryDto_optimazation());
    }

    /**
     * 주문 상품 추가
     */
    @Transactional
    public OrderResponseDto addOrderItems(Long orderId, Long itemId, int quantity) {
        // 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다. id = " + orderId)
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다. id = " + itemId)
        );

        // 주문 상품 생성.
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), quantity);

        // 주문 상품 추가.
        order.addOrderItem(orderItem);

        return new OrderResponseDto(orderRepository.save(order));
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다. id = " + orderId)
        );

        // 주문 취소
        order.cancel();
    }
}
