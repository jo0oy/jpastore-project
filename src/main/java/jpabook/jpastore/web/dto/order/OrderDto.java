package jpabook.jpastore.web.dto.order;

import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.Pay;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDto {
    @ToString
    @Getter
    @Builder
    public static class OrderRegisterReq {

        @NotNull
        private Long memberId;

        private List<OrderItemRegisterReq> orderItems;

        @NotBlank
        private String city;

        @NotBlank
        private String street;

        @NotBlank
        private String zipcode;

        @NotNull
        private Pay payInfo;
    }

    @ToString
    @Getter
    @Builder
    public static class OrderItemRegisterReq {

        @NotNull
        private Long itemId;

        @NotNull
        @Range(min = 1, message = "{Range.orderItem.quantity}")
        private Integer quantity;
    }

    @ToString
    @Getter
    @Builder
    public static class RegisterSuccessResponse {
        private Long registeredOrderId;
    }

    @ToString
    @Getter
    @Builder
    public static class SimpleInfoResponse {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate;
        private String orderStatus;
        private String deliveryStatus;
        private AddressInfoResponse address;
    }

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate;
        private String orderStatus;
        private String deliveryStatus;
        private Integer totalPrice;
        private List<OrderItemInfoResponse> orderItems;
    }

    @ToString
    @Getter
    @Builder
    public static class OrderItemInfoResponse {
        private String itemName;
        private Integer orderPrice;
        private Integer quantity;
    }

    @ToString
    @Getter
    @Builder
    public static class AddressInfoResponse {
        private String city;
        private String street;
        private String zipcode;
    }

    @Getter
    public static class ListResponse<T> {
        private int totalCount;
        private List<T> list = new ArrayList<>();

        public ListResponse(List<T> list) {
            this.totalCount = list.size();
            this.list.addAll(list);
        }
    }

    @ToString
    @Getter
    @Builder
    public static class OrderSearchCondition {
        private Long memberId;
        private String memberName;
        private Long orderId;
        private OrderStatus status;
        private DeliveryStatus deliveryStatus;
    }
}
