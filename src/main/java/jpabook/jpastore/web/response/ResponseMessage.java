package jpabook.jpastore.web.response;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    LOGIN_SUCCESS("로그인 성공"),
    LOGIN_FAIL("로그인 실패"),
    CREATED_MEMBER("회원가입 성공"),
    READ_MEMBER("단일 회원 조회 성공"),
    READ_MEMBERS("회원 리스트 조회 성공"),
    NOT_FOUND_MEMBER("회원정보를 찾을 수 없음."),
    UPDATED_MEMBER("회원정보 수정 성공"),
    DELETE_MEMBER("회원 탈퇴 성공"),
    CREATED_ORDER("주문 성공"),
    READ_ORDER("단일 주문 조회 성공"),
    READ_ORDERS("주문 리스트 조회 성공"),
    NOT_FOUND_ORDER("주문 정보를 찾을 수 없음."),
    UPDATED_ORDER("주문정보 수정 성공"),
    CANCEL_ORDER("주문 취소 성공"),
    CREATED_ITEM("상품 등록 성공"),
    READ_ITEM("단일 상품 조회 성공"),
    READ_ITEMS("상품 리스트 조회 성공"),
    CREATED_CATEGORY("카테고리 생성 성공"),
    READ_CATEGORY("단일 카테고리 조회 성공"),
    UPDATED_CATEGORY("카테고리 수정 성공"),
    READ_CATEGORY_LIST("전체 카테고리 리스트 조회 성공"),
    READ_CATEGORY_ITEMS("카테고리별 상품 리스트 조회 성공");

    String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
