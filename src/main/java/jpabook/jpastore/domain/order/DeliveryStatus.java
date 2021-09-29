package jpabook.jpastore.domain.order;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    READY("배송 준비중"), DELIVERY_ING("배송중"), COMPLETE("배송 완료");

    String message;

    DeliveryStatus(String message) {
        this.message = message;
    }
}
