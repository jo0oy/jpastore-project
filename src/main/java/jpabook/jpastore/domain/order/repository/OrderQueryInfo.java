package jpabook.jpastore.domain.order.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderQueryInfo {

    @ToString
    @NoArgsConstructor
    @Setter
    @Getter
    public static class MainInfo {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate;
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private AddressInfo address;
        private List<OrderItemInfo> orderItems = new ArrayList<>();

        public MainInfo(Long orderId,
                        String memberName,
                        LocalDateTime orderedDate,
                        OrderStatus orderStatus,
                        DeliveryStatus deliveryStatus,
                        Address address) {
            this.orderId = orderId;
            this.memberName = memberName;
            this.orderedDate = orderedDate;
            this.orderStatus = orderStatus;
            this.deliveryStatus = deliveryStatus;
            this.address = new AddressInfo(address);
        }
    }

    @ToString
    @NoArgsConstructor
    @Setter
    @Getter
    public static class SimpleInfo {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderedDate; //주문시간
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private AddressInfo address;

        public SimpleInfo(Long orderId,
                          String memberName,
                          LocalDateTime orderedDate,
                          OrderStatus orderStatus,
                          DeliveryStatus deliveryStatus,
                          Address address) {
            this.orderId = orderId;
            this.memberName = memberName;
            this.orderedDate = orderedDate;
            this.orderStatus = orderStatus;
            this.deliveryStatus = deliveryStatus;
            this.address = new AddressInfo(address);
        }
    }

    @ToString
    @NoArgsConstructor
    @Getter
    public static class OrderItemInfo {
        @JsonIgnore
        private Long orderId;
        private String itemName;
        private int orderPrice;
        private int quantity;

        public OrderItemInfo(Long orderId,
                             String itemName,
                             Money orderPrice,
                             int quantity) {
            this.orderId = orderId;
            this.itemName = itemName;
            this.orderPrice = orderPrice.getValue();
            this.quantity = quantity;
        }
    }

    @ToString
    @NoArgsConstructor
    @Getter
    public static class AddressInfo {
        private String city;
        private String street;
        private String zipcode;

        public AddressInfo(String city,
                           String street,
                           String zipcode) {
            this.city = city;
            this.street = street;
            this.zipcode = zipcode;
        }

        public AddressInfo(Address address) {
            this.city = address.getCity();
            this.street = address.getStreet();
            this.zipcode = address.getZipcode();
        }
    }
}
