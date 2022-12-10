package jpabook.jpastore.application.order;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderInfo {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate;
        private String orderStatus;
        private String deliveryStatus;
        private Money totalPrice;
        private List<OrderItemInfo> orderItems;
        private AddressInfo addressInfo;

        public MainInfo(Order entity) {
            this.orderId = entity.getId();
            this.memberName = entity.getMember().getUsername();
            this.orderedDate = entity.getCreatedDate();
            this.orderStatus = entity.getStatus().getMessage();
            this.deliveryStatus = entity.getDelivery().getStatus().getMessage();
            this.totalPrice = entity.getTotalPrice();
            orderItems = entity.getOrderItems().stream()
                    .map(OrderItemInfo::new).collect(Collectors.toList());
            this.addressInfo = new AddressInfo(entity.getDelivery().getAddress());
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class SimpleInfo {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate;
        private String orderStatus;
        private String deliveryStatus;
        private AddressInfo address;

        public SimpleInfo(Order entity) {
            this.orderId = entity.getId();
            this.memberName = entity.getMember().getUsername();
            this.orderedDate = entity.getCreatedDate();
            this.orderStatus = entity.getStatus().getMessage();
            this.deliveryStatus = entity.getDelivery().getStatus().getMessage();
            this.address = new AddressInfo(entity.getDelivery().getAddress());
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class OrderItemInfo {
        private String itemName;
        private Money orderPrice;
        private Integer quantity;

        public OrderItemInfo(OrderItem entity) {
            this.itemName = entity.getItem().getName();
            this.orderPrice = entity.getOrderPrice();
            this.quantity = entity.getQuantity();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class AddressInfo {
        private String city;
        private String street;
        private String zipcode;

        public AddressInfo(Address address) {
            this.city = address.getCity();
            this.street = address.getStreet();
            this.zipcode = address.getZipcode();
        }
    }

}
