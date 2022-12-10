package jpabook.jpastore.common.response;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    //AUTH
    //SUCCESS MESSAGE
    LOGIN_SUCCESS("로그인 성공"),
    REISSUE_SUCCESS("액세스 토큰 재발급 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),

    // MEMBER
    // SUCCESS MESSAGE
    JOIN_MEMBER("회원가입 성공"),
    READ_MEMBER("단일 회원 조회 성공"),
    READ_MEMBERS("회원 리스트 조회 성공"),
    UPDATE_MEMBER("회원정보 수정 성공"),
    DELETE_MEMBER("회원 탈퇴 성공"),

    // ERROR MESSAGE
    NOT_FOUND_MEMBER("회원정보를 찾을 수 없음."),

    // MEMBERSHIP
    // SUCCESS MESSAGE
    UPDATE_MEMBERSHIP("회원 멤버십 업데이트 성공"),


    // ORDER
    // SUCCESS MESSAGE
    REGISTER_ORDER("주문 등록 성공"),
    READ_ORDER("단일 주문 조회 성공"),
    READ_ORDERS("주문 리스트 조회 성공"),
    UPDATED_ORDER("주문정보 수정 성공"),
    CANCEL_ORDER("주문 취소 성공"),

    // ERROR MESSAGE
    NOT_FOUND_ORDER("주문 정보를 찾을 수 없음."),


    // ITEM
    // SUCCESS MESSAGE
    REGISTER_ITEM("상품 등록 성공"),
    UPDATE_ITEM("상품 정보 수정 성공"),
    READ_ITEM("단일 상품 조회 성공"),
    READ_ITEMS("상품 리스트 조회 성공"),
    DELETE_ITEM("상품 삭제 성공"),

    // ERROR MESSAGE
    NOT_FOUND_ITEM("상품 정보를 찾을 수 없음."),


    // CATEGORY
    // SUCCESS MESSAGE
    REGISTER_CATEGORY("카테고리 생성 성공"),
    READ_CATEGORY("단일 카테고리 조회 성공"),
    UPDATE_CATEGORY("카테고리 수정 성공"),
    DELETE_CATEGORY("카테고리 삭제 성공"),
    READ_CATEGORY_LIST("전체 카테고리 리스트 조회 성공"),
    READ_CATEGORY_ITEMS("카테고리별 상품 리스트 조회 성공"),

    // ERROR MESSAGE
    NOT_FOUND_CATEGORY("카테고리 정보를 찾을 수 없음."),


    // REVIEW
    // SUCCESS MESSAGE
    REGISTER_REVIEW("리뷰 등록 성공"),
    UPDATE_REVIEW("리뷰 수정 성공"),
    READ_REVIEW("단일 리뷰 조회 성공"),
    READ_REVIEWS("리뷰 리스트 조회 성공"),
    DELETE_REVIEW("리뷰 삭제 성공"),

    // ERROR MESSAGE
    NOT_FOUND_REVIEW("리뷰를 찾을 수 없음.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
