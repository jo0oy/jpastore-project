package jpabook.jpastore.application.order;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.common.utils.PageRequestUtils;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.order.*;
import jpabook.jpastore.domain.order.repository.OrderQueryInfo;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 주문
     */
    // OrderRegisterReq 이용한 주문 생성
    @Override
    @Transactional
    public Long order(OrderCommand.OrderRegisterReq command) {
        log.info("saving order");

        // 엔티티 조회
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(
                () -> {
                    log.error("존재하지 않는 회원입니다. id = {}", command.getMemberId());
                    throw new EntityNotFoundException("존재하지 않는 회원입니다. id : " + command.getMemberId());
                });

        // 주문 상품 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCommand.OrderItemRegisterReq orderItemReq : command.getOrderItems()) {
            Long itemId = orderItemReq.getItemId();
            int quantity = orderItemReq.getQuantity();
            // 상품 엔티티 조회
            Item item = itemRepository.findItemForUpdate(itemId).orElseThrow(
                    () -> {
                        log.error("존재하지 않는 상품입니다. id = {} " , itemId);
                        throw new EntityNotFoundException("존재하지 않는 상품입니다. id = " + itemId);
                    });

            orderItems.add(OrderItem.createOrderItem(item, item.getPrice(), quantity));
        }

        // 배송정보 생성 및 주문 상태 설정
        // 실시간 계좌이체 : 주문 상태 = 결제 대기중 & 배송 상태 = NONE
        // 나머지 결제 수단 : 주문 상태 = 주문 완료 & 배송 상태 = 배송 준비중
        Pay payInfo = command.getPayInfo();
        DeliveryStatus deliveryStatus = setDeliveryStatusByPayInfo(payInfo);
        OrderStatus orderStatus = setOrderStatusByPayInfo(payInfo);

        Delivery delivery = Delivery.builder()
                .address(new Address(command.getCity(), command.getStreet(), command.getZipcode()))
                .status(deliveryStatus)
                .build();

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItems, payInfo, orderStatus);

        // 주문 완료일 경우 멤버십의 totalSpending 업데이트
        if (orderStatus == OrderStatus.ORDER) {
            member.getMembership().addTotalSpending(order.getTotalPrice());
        }

        return orderRepository.save(order).getId();
    }

    /**
     * 단일 주문 조회
     */

    // 1. spring data jpa 기본 메소드 활용 -> dto 에서 연관관계 매핑 엔티티에 접근하기 때문에 N+1 문제 발생!
    // 글로벌 배치 설정으로 문제 해결!
    @Override
    public OrderInfo.MainInfo getOrder(Long orderId, String authUsername) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new EntityNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
        });

        checkAuthority(order, authUsername);

        return new OrderInfo.MainInfo(order);
    }

    // 2. 일대일 매핑 관계(member, delivery) 페치조인
    // 단일 주문 간단 정보 조회
    @Override
    public OrderInfo.SimpleInfo getOrderSimpleInfo(Long orderId, String authUsername) {
        Order order = orderRepository.findOrderWithMemberDelivery(orderId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new EntityNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
                });

        checkAuthority(order, authUsername);

        return new OrderInfo.SimpleInfo(order);
    }

    // 3. 일대일 매핑 관계(member, delivery) 페치조인 -> 나머지 컬렉션은 글로벌 배치 설정으로 in query
    @Override
    public OrderInfo.MainInfo getOrderFetch(Long orderId, String authUsername) {
        Order order = orderRepository.findOrderWithMemberDelivery(orderId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문입니다. id = {}", orderId );
                    throw new EntityNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
                });

        checkAuthority(order, authUsername);

        return new OrderInfo.MainInfo(order);
    }

    /**
     * 전체 주문 리스트 조회
     */

    // 1-1. ToOne 관계(member, delivery) 페치조인 - List 조회
    @Override
    public List<OrderInfo.MainInfo> listOrder() {
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(OrderInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    // 1-2. ToOne 관계(member, delivery) 페치조인 - Page 조회
    @Override
    public Page<OrderInfo.MainInfo> listOrder(Pageable pageable) {

        return orderRepository.findAllWithMemberDelivery(PageRequestUtils.of(pageable))
                .map(OrderInfo.MainInfo::new);
    }

    // 1-3. ToOne 관계(member, delivery) 페치조인 - 간단 정보 조회(컬렉션 접근 없음)
    @Override
    public Page<OrderInfo.SimpleInfo> listSimpleOrder(Pageable pageable) {
        return orderRepository.findAllWithMemberDelivery(PageRequestUtils.of(pageable))
                .map(OrderInfo.SimpleInfo::new);
    }

    // 1-4. ToOne, ToMany 모두 페치조인 - 'distinct' 키워드 없음
    // 카타시안 곱의 결과 반환!
    @Override
    public List<OrderInfo.MainInfo> listOrderFetchOrderItems() {
        return orderRepository.findAllWithOrderItems()
                .stream()
                .map(OrderInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    // 1-5. ToOne, ToMany 모두 페치조인 - 'distinct' 키워드 있음
    // 정상 결과 반환, 하지만 애플리케이션 상에서 distinct 처리하기 때문에 성능상 not good
    @Override
    public List<OrderInfo.MainInfo> listOrderFetchOrderItemsDistinct() {
        return orderRepository.findAllWithOrderItemsDistinct()
                .stream()
                .map(OrderInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    // 1-6. 전체 주문 리스트 조회 (페이징, 정렬, 검색 기능)
    // ToOne(Member, Delivery)은 모두 페치조인 후 글로벌 패치 설정을 통한 in query 적용
    @Override
    public Page<OrderInfo.MainInfo> listOrder(OrderCommand.OrderSearchCondition condition, Pageable pageable) {
        return orderRepository.findByCondition(condition.toSearchCondition(), PageRequestUtils.of(pageable))
                .map(OrderInfo.MainInfo::new);
    }

    // 1-7. DTO(OrderQueryInfo.SimpleInfo)로 쿼리 직접 조회
    @Override
    public List<OrderQueryInfo.SimpleInfo> listOrderSimpleInfos() {
        return orderRepository.findAllOrderSimpleInfo();
    }

    // 1-7. DTO(OrderQueryInfo.MainInfo)로 쿼리 직접 조회
    @Override
    public List<OrderQueryInfo.MainInfo> listOrderQueryInfos() {
        return orderRepository.findAllOrderInfo();
    }

    /**
     * 배달 상태 변경
     */
    @Override
    @Transactional
    public void changeDeliveryStatus(Long orderId, DeliveryStatus status) {
        log.info("changing delivery status...");
        Order order = orderRepository.findOrderWithDelivery(orderId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문 정보입니다. id = {}", orderId);
                    throw new EntityNotFoundException("존재하지 않는 주문 정보입니다. id = " + orderId);
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
            throw new EntityNotFoundException("존재하지 않는 주문입니다, id = " + orderId);
        });

        log.info("before change order status = {}", order.getStatus());

        order.updateStatus(status);
    }

    /**
     * 주문 취소
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId, String authUsername) {
        log.info("cancel order...");

        // 주문 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("존재하지 않는 주문입니다. id = {}", orderId);
            throw new EntityNotFoundException("존재하지 않는 주문입니다. id = " + orderId);
        });

        // 주문 취소 권한 확인 로직 실행
        checkAuthority(order, authUsername);

        // 주문 취소
        order.cancel();
    }

    private DeliveryStatus setDeliveryStatusByPayInfo(Pay payInfo) {
        return (payInfo == Pay.BANK_TRANS) ? DeliveryStatus.NONE : DeliveryStatus.PREPARING;
    }

    private OrderStatus setOrderStatusByPayInfo(Pay payInfo) {
        return (payInfo == Pay.BANK_TRANS) ? OrderStatus.PAYMENT_WAITING : OrderStatus.ORDER;
    }

    // 주문 접근 권한 검증 메서드
    private void checkAuthority(Order order, String username) {
        var member = memberRepository.findByUsername(username).orElseThrow(
                () -> {
                    log.error("존재하지 않는 회원입니다. username = {} " , username);
                    throw new EntityNotFoundException("존재하지 않는 회원입니다. username = " + username);
                });

        if (!order.hasAuthority(member)) {
            log.error("주문에 대한 접근 권한이 없습니다. orderId={}, username={}", order.getId(), username);
            throw new AccessDeniedException("해당 주문에 대한 접근 권한이 없습니다.");
        }
    }
}
