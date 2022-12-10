package jpabook.jpastore.application.order;

import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.Pay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class OrderCommand {

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class OrderRegisterReq {
        private Long memberId;
        private List<OrderItemRegisterReq> orderItems;
        private String city;
        private String street;
        private String zipcode;
        private Pay payInfo;
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class OrderItemRegisterReq {
        private Long itemId;
        private Integer quantity;
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class OrderSearchCondition {
        private Long memberId;
        private String memberName;
        private Long orderId;
        private OrderStatus status;
        private DeliveryStatus deliveryStatus;

        // repository 에 사용될 condition 객체로 변환
        public jpabook.jpastore.domain.order.OrderSearchCondition toSearchCondition() {
            return jpabook.jpastore.domain.order.OrderSearchCondition
                    .builder()
                    .memberId(memberId)
                    .memberName(memberName)
                    .orderId(orderId)
                    .status(status)
                    .deliveryStatus(deliveryStatus)
                    .build();
        }
    }
}
