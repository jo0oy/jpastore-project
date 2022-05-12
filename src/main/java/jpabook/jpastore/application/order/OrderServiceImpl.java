package jpabook.jpastore.application.order;

import jpabook.jpastore.domain.order.repository.OrderRepository;
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
import jpabook.jpastore.exception.ItemNotFoundException;
import jpabook.jpastore.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 주문
     */
    // api controller 에서 request dto unwrap 하여 보낸 정보로 주문
    @Override
    @Transactional
    public Long order(Long memberId, Long itemId, int quantity, Pay payInfo){
        // 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> {
                    log.error("존재하지 않는 회원입니다. id = {}", memberId);
                    throw new MemberNotFoundException("존재하지 않는 회원입니다. id : " + memberId);
                });
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.error("존재하지 않는 상품입니다. id = {} " , itemId);
                    throw new ItemNotFoundException("존재하지 않는 상품입니다. id = " + itemId);
                });

        // 배송정보 생성 및 주문 상태 설정
        // 실시간 계좌이체 : 주문 상태 = 결제 대기중 & 배송 상태 = NONE
        // 나머지 결제 수단 : 주문 상태 = 주문 완료 & 배송 상태 = 배송 준비중
        DeliveryStatus deliveryStatus = setDeliveryStatusByPayInfo(payInfo);
        OrderStatus orderStatus = setOrderStatusByPayInfo(payInfo);

        Delivery delivery = Delivery.builder()
                .address(member.getAddress())
                .status(deliveryStatus)
                .build();

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), quantity);
        // 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        Order order = Order.createOrder(member, delivery, orderItems, payInfo, orderStatus);

        return orderRepository.save(order).getId();
    }

    // OrderRequestDto 이용한 주문 생성
    @Override
    @Transactional
    public Long order(OrderSaveRequestDto requestDto) {

        // 엔티티 조회
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(
                () -> {
                    log.error("존재하지 않는 회원입니다. id = {}", requestDto.getMemberId());
                    throw new MemberNotFoundException("존재하지 않는 회원입니다. id : " + requestDto.getMemberId());
                });

        // 주문 상품 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto orderItemDto : requestDto.getOrderItems()) {
            Long itemId = orderItemDto.getItemId();
            int quantity = orderItemDto.getQuantity();
            // 상품 엔티티 조회
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> {
                        log.error("존재하지 않는 상품입니다. id = {} " , itemId);
                        throw new ItemNotFoundException("존재하지 않는 상품입니다. id = " + itemId);
                    });

            orderItems.add(OrderItem.createOrderItem(item, item.getPrice(), quantity));
        }

        // 배송정보 생성 및 주문 상태 설정
        // 실시간 계좌이체 : 주문 상태 = 결제 대기중 & 배송 상태 = NONE
        // 나머지 결제 수단 : 주문 상태 = 주문 완료 & 배송 상태 = 배송 준비중
        Pay payInfo = requestDto.getPayInfo();
        DeliveryStatus deliveryStatus = setDeliveryStatusByPayInfo(payInfo);
        OrderStatus orderStatus = setOrderStatusByPayInfo(payInfo);

        Delivery delivery = Delivery.builder()
                .address(member.getAddress())
                .status(deliveryStatus)
                .build();

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItems, payInfo, orderStatus);

        return orderRepository.save(order).getId();
    }

    /**
     * 단일 주문 조회
     */
    // 1. Spring Data JPA 기본 제공 메서드를 통한 조회
    // -> Response dto 에서 매핑된 엔티티 정보에 직접 접근하면서 N+1 쿼리 문제 발생!
    @Override
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다. id = " + orderId)
        );

        return new OrderResponseDto(order);
    }

    // 2. 일대일 매핑 관계 엔티티들(member, delivery) 모두 페치조인, repository 조회 결과 simple dto 로 반환
    @Override
    public OrderSimpleDto getOrderWithMemberDelivery(Long orderId) {
        Order order = orderRepository.findOrderWithMemberDelivery(orderId);

        return new OrderSimpleDto(order);
    }

    /**
     * 전체 주문 조회
     */

    // 1. 엔티티를 DTO로 변환
    @Override
    public OrderListResponseDto<?> listOrder(){
        List<Order> orders = orderRepository.findAll();

        return new OrderListResponseDto<>(orders);
    }

    // 2.ToOne 관계 (Member, Delivery) 페치조인을 사용한 간단 조회
    @Override
    public List<OrderSimpleDto> listOrderWithMemberDelivery(){
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(OrderSimpleDto::new)
                .collect(Collectors.toList());
    }

    // 3. JPA에서 DTO를 직접 조회한 간단 조회
    @Override
    public OrderListResponseDto<?> listOrderDtos() {
        List<OrderSimpleQueryDto> orders = orderRepository.findAllOrderSimpleDto();

        return new OrderListResponseDto<>(orders);
    }

    // 4. ToMany (OrderItems, Items)에 페치조인을 사용한 전체 조회
    @Override
    public OrderListResponseDto<?> listOrderWithItems() {
        List<Order> orders = orderQueryRepository.findAllWithItems();
        return new OrderListResponseDto<>(orders);
    }

    // 5. distinct 키워드를 사용한 컬렉션 페치 조인
    @Override
    public OrderListResponseDto<?> listOrderWithItemsDistinct() {
        List<Order> orders = orderQueryRepository.findAllWithItemsDistinct();
        return new OrderListResponseDto<>(orders);
    }

    @Override
    public Page<OrderResponseDto> findAllWithItemsPaging(int offset, int limit) {
        return orderRepository
                .findAllWithMemberDelivery(PageRequest.of(offset, limit))
                .map(OrderResponseDto::new);
    }

    @Override
    public OrderListResponseDto<?> listOrderByDto() {
        return new OrderListResponseDto<>(orderQueryRepository.findOrderQueryDto());
    }

    @Override
    public OrderListResponseDto<?> listOrderByDto_optimize() {
        return new OrderListResponseDto<>(orderQueryRepository.findOrderQueryDto_optimazation());
    }

    /**
     * 주문 상품 추가
     */
    @Override
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
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 주문입니다. id = " + orderId)
        );

        // 주문 취소
        order.cancel();
    }

    private DeliveryStatus setDeliveryStatusByPayInfo(Pay payInfo) {
        return (payInfo == Pay.BANK_TRANS) ? DeliveryStatus.NONE : DeliveryStatus.PREPARING;
    }

    private OrderStatus setOrderStatusByPayInfo(Pay payInfo) {
        return (payInfo == Pay.BANK_TRANS) ? OrderStatus.PAYMENT_WAITING : OrderStatus.ORDER;
    }
}
