package jpabook.jpastore.domain.order;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    COMPLETE("배송 완료", null),
    DELIVERING("배송중", COMPLETE),
    READY("배송 대기중", DELIVERING),
    PREPARING("배송 준비중", READY),
    NONE("-", PREPARING);

    private final String message;
    private final DeliveryStatus next;

    DeliveryStatus(String message, DeliveryStatus next) {
        this.message = message;
        this.next = next;
    }
}
