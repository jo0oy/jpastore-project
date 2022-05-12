package jpabook.jpastore.application.order;

import jpabook.jpastore.application.dto.order.OrderResponseDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.order.*;
import jpabook.jpastore.domain.order.repository.DeliveryRepository;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import jpabook.jpastore.dto.order.OrderItemRequestDto;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import jpabook.jpastore.exception.ItemNotFoundException;
import jpabook.jpastore.exception.MemberNotFoundException;
import jpabook.jpastore.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService2Impl implements OrderService2 {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;

    /**
     * 주문
     */
    // api controller 에서 request dto unwrap 하여 보낸 정보로 주문
    @Override
    @Transactional
    public Long order(Long memberId, Long itemId, int quantity, Pay payInfo){

        log.info("saving order");

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
        log.info("saving order");

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

    // 1. spring data jpa 기본 메소드 활용 -> dto 에서 연관관계 매핑 엔티티에 접근하기 때문에 N+1 문제 발생!
    @Override
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new OrderNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
        });

        return new OrderResponseDto(order);
    }

    // 2. 일대일 매핑 관계(member, delivery) 페치조인
    @Override
    public OrderSimpleDto getOrderFetchSimpleDto(Long orderId) {
        Order order = Optional.ofNullable(orderRepository.findOrderWithMemberDelivery(orderId))
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new OrderNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
                });

        return new OrderSimpleDto(order);
    }

    @Override
    public OrderResponseDto getOrderFetch(Long orderId) {
        Order order = Optional.ofNullable(orderRepository.findOrderWithMemberDelivery(orderId))
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new OrderNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
                });

        return new OrderResponseDto(order);
    }

    /**
     * 전체 주문 리스트 조회
     */

    // 1-1. ToOne 관계(member, delivery) 페치조인 - List 조회
    @Override
    public List<OrderResponseDto> listOrder() {
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    // 1-2. ToOne 관계(member, delivery) 페치조인 - Page 조회
    @Override
    public Page<OrderResponseDto> listOrder(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return orderRepository.findAllWithMemberDelivery(pageRequest)
                .map(OrderResponseDto::new);
    }

    /**
     * 배달 상태 변경
     */
    @Override
    @Transactional
    public void changeDeliveryStatus(Long orderId, DeliveryStatus status) {
        log.info("changing delivery status...");
        Order order = Optional.ofNullable(orderRepository.findOrderWithDelivery(orderId))
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문 정보입니다. id = {}", orderId);
                    throw new OrderNotFoundException("존재하지 않는 주문 정보입니다. id = " + orderId);
                });

        order.getDelivery().changeStatus(status);
    }

    /**
     * 주문 상태 변경
     */
    @Override
    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus status) {
        log.info("changing order status...");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("존재하지 않는 주문입니다, id = {}", orderId);
            throw new OrderNotFoundException("존재하지 않는 주문입니다, id = " + orderId);
        });

        log.info("before change order status = {}", order.getStatus());

        order.updateStatus(status);
    }

    /**
     * 주문 취소
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("cancel order...");

        // 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("존재하지 않는 주문입니다. id = {}", orderId);
            throw new OrderNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
        });

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
