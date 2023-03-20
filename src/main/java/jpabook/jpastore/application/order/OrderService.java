package jpabook.jpastore.application.order;

import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.repository.OrderQueryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Long order(OrderCommand.OrderRegisterReq command);

    /**
     * 단일 주문 조회
     */
    // 1. spring data jpa 기본 메소드 활용 -> dto 에서 연관관계 매핑 엔티티에 접근하기 때문에 N+1 문제 발생!
    OrderInfo.MainInfo getOrder(Long orderId, String authUsername);

    // 2. Member, Delivery 페치 조인 조회 후 simple info(1:1 연관관계 정보만 포함한 dto) 로 변환.
    OrderInfo.SimpleInfo getOrderSimpleInfo(Long orderId, String authUsername);

    // 3. getOrder 페치조인 통해 조회 (Member, Delivery), default_batch_fetch 설정으로 in 쿼리 발생.
    OrderInfo.MainInfo getOrderFetch(Long orderId, String authUsername);

    /**
     * 전체 주문 조회
     */

    // 1-1. 주문 리스트 조회 - ToOne(Member, Delivery) 페치 조인
    List<OrderInfo.MainInfo> listOrder();

    // 1-2. 주문 리스트 조회 - ToOne(Member, Delivery) 페치 조인 + Paging
    Page<OrderInfo.MainInfo> listOrder(Pageable pageable);

    // 2. 주문 simple 정보 리스트 조회 - ToOne(Member, Delivery) 페치 조인 + Paging
    Page<OrderInfo.SimpleInfo> listSimpleOrder(Pageable pageable);

    // 3-1. 주문 리스트 조회 - ToOne(Member, Delivery) + [ToMany(OrderItem) 컬렉션] 페치 조인
    List<OrderInfo.MainInfo> listOrderFetchOrderItems();

    // 3-2. 주문 리스트 조회
    // ToOne(Member, Delivery) + [ToMany(OrderItem) 컬렉션] 페치 조인 + 'distinct' 키워드 추가
    List<OrderInfo.MainInfo> listOrderFetchOrderItemsDistinct();

    // 4. 검색 조건에 따른 주문 리스트 조회
    // ToOne(Member, Delivery) 페치 조인 + 검색 조건(OrderSearchCondition) + Paging
    Page<OrderInfo.MainInfo> listOrder(OrderCommand.OrderSearchCondition condition, Pageable pageable);

    // 5-1. 주문 simple 정보 리스트 조회 - 쿼리에서 DTO 직접 조회한 간단 조회
    List<OrderQueryInfo.SimpleInfo> listOrderSimpleInfos();

    // 5-2. 주문 리스트 조회 - 쿼리에서 DTO 직접 조회 (mainInfo)
    // orderItemMap 을 활용한 최적화 -> 1 + 1 쿼리 호출
    List<OrderQueryInfo.MainInfo> listOrderQueryInfos();

    void changeDeliveryStatus(Long orderId, DeliveryStatus status);

    void changeOrderStatus(Long orderId, OrderStatus status);

    // 주문 취소
    void cancelOrder(Long orderId, String authUsername);
}
