package jpabook.jpastore.application.order;

import jpabook.jpastore.application.dto.order.OrderListResponseDto;
import jpabook.jpastore.application.dto.order.OrderResponseDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.domain.order.Pay;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    // api controller 에서 request dto unwrap 하여 보낸 정보로 주문
    Long order(Long memberId, Long itemId, int quantity, Pay payInfo);

    // OrderRequestDto 이용한 주문 생성
    Long order(OrderSaveRequestDto requestDto);

    // 1. Spring Data JPA 기본 제공 메서드를 통한 조회
    // -> Response dto 에서 매핑된 엔티티 정보에 직접 접근하면서 N+1 쿼리 문제 발생!
    OrderResponseDto getOrder(Long orderId);

    // 2. 일대일 매핑 관계 엔티티들(member, delivery) 모두 페치조인, repository 조회 결과 simple dto 로 반환
    OrderSimpleDto getOrderWithMemberDelivery(Long orderId);

    // 1. 엔티티를 DTO로 변환
    OrderListResponseDto<?> listOrder();

    // 2.ToOne 관계 (Member, Delivery) 페치조인을 사용한 간단 조회
    List<OrderSimpleDto> listOrderWithMemberDelivery();

    // 3. JPA에서 DTO를 직접 조회한 간단 조회
    OrderListResponseDto<?> listOrderDtos();

    // 4. ToMany (OrderItems, Items)에 페치조인을 사용한 전체 조회
    OrderListResponseDto<?> listOrderWithItems();

    // 5. distinct 키워드를 사용한 컬렉션 페치 조인
    OrderListResponseDto<?> listOrderWithItemsDistinct();

    Page<OrderResponseDto> findAllWithItemsPaging(int offset, int limit);

    OrderListResponseDto<?> listOrderByDto();

    OrderListResponseDto<?> listOrderByDto_optimize();

    OrderResponseDto addOrderItems(Long orderId, Long itemId, int quantity);

    void cancelOrder(Long orderId);
}
