package jpabook.jpastore.domain.order;

import lombok.Getter;

@Getter
public enum Pay {
    INSTANT_CASH("실시간 계좌이체"),
    BANK_TRANS("무통장 입금"),
    CARD("신용/체크카드"),
    COUPON("모바일 쿠폰"),
    NAVER_PAY("네이버 페이"),
    KAKAO_PAY("카카오 페이");

    private final String payDesc;

    Pay(String payDesc) {
        this.payDesc = payDesc;
    }
}
