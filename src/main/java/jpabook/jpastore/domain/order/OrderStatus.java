package jpabook.jpastore.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDER("주문 완료"),
    PAYMENT_WAITING("결제 대기중"),
    CANCEL("주문 취소");

    String message;

    OrderStatus(String message) {
        this.message = message;
    }
}
