package jpabook.jpastore.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDER("주문 완료"), CANCEL("주문 취소");

    String message;

    OrderStatus(String message) {
        this.message = message;
    }
}
