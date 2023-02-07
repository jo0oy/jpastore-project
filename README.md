# 온라인 서점 서비스 API - jpastore-project

## 요구사항 및 상세 기능 

* 인증 기능
    * 로그인
    * 토큰 재발행
    * 소셜로그인 (Kakao/Naver)
    * 로그아웃
* 회원 기능
    * 회원 등록
    * 회원 조회 - 회원 본인 or 관리자 권한
    * 회원 멤버십 벌크 업데이트 - 관리자 권한
    * 회원 삭제 - 회원 본인 or 관리자 권한
    * 전체 회원 리스트 조회 - 관리자 권한
    * 전체 회원 리스트 조회 (페이징, 정렬, 검색) - 관리자 권한
* 상품 기능
    * 상품 등록 - 관리자 권한
    * 상품 수정 - 관리자 권한
    * 상품 삭제 - 관리자 권한
    * 단일 상품 조회
    * 전체 상품 리스트 조회
    * 전체 상품 리스트 조회 (페이징, 정렬, 검색)
* 카테고리 기능
    * 카테고리 등록 - 관리자 권한
    * 카테고리 수정 - 관리자 권한
    * 카테고리 삭제 - 관리자 권한
    * 전체 카테고리 조회
    * 전체 카테고리 계층 리스트 조회
    * 카테고리 상품 리스트 조회
* 주문 기능
    * 상품 주문
    * 단일 주문 내역 조회 - 회원 본인 or 관리자 권한
    * 주문 취소 - 회원 본인 or 관리자 권한
    * 로그인된 회원의 주문 내역 리스트 조회
    * 전체 주문 리스트 조회 - 관리자 권한
    * 전체 주문 리스트 조회 (페이징, 정렬, 검색) - 관리자 권한
* 리뷰 기능
    * 리뷰 작성
    * 단일 리뷰 조회
    * 로그인된 회원이 작성한 리뷰 리스트 조회
    * 전체 리뷰 리스트 조회
    * 전체 리뷰 리스트 조회 (페이징, 정렬, 검색)
* 기타 요구사항
    * JWT 토큰을 통해 인증/권한 관리를 진행한다.
    * 소셜로그인(카카오/네이버) 가능하다.
    * RefreshToken은 Redis에 “RT:{회원ID}”를 key 값으로 저장된다. → 만료기한(TTL) 있음.
    * 상품은 재고 관리가 필요하다.
    * 상품의 종류는 도서, 음반, 영화가 있다.
    * 상품을 카테고리로 구분할 수 있다.
    * 회원 멤버십은 상/하반기 분기별로 회원의 이전 분기 누적 지출액에 따라 등급을 업데이트 한다.
    * 등록하려는 카테고리가 root 레벨일 경우 어느 것과도 중복되지 않은 카테고리명만 등록 가능하다. 그 외일 경우, 부모 카테고리, 형제 카테고리와  중복되지 않은 이름만 등록 가능하다.
    * 자식 카테고리가 있을 경우 해당 카테고리는 삭제할 수 없다.
    * CategoryItem 엔티티를 제외한 모든 도메인의 삭제는 softDelete 처리한다.
    
## 개발환경

* IntelliJ Ultimate 2022.03
* Java 11
* Gradle 6.8.3
* Spring Boot 2.6.11

## 세부 기술 스택

Spring Boot

* Spring Web
* Spring Data JPA
* Spring Security 
* Spring Security OAuth2
* Spring AOP
* Spring Validation
* Spring Dev Tools
* Thymeleaf
* MariaDB Driver
* Lombok

그외 

* Redis
* JWT
* Querydsl
* MapStruct
* Testcontainers
* Google Guava
* Spring Doc

## ERD

<img width="97%" src="https://user-images.githubusercontent.com/55842092/215992342-765a8675-4055-4b0b-b865-12a975094281.png">
<img width="97%" height="80%" src="https://user-images.githubusercontent.com/55842092/215993147-240d1a35-5446-40ee-a6d2-f0c3be083735.png">

## Feature List

* 상품(Item) 과 Book, Album, Dvd `상속 관계 매핑` 적용 - `단일 테이블` 전략
* 상품 재고 동시성 문제를 고려해 상품(Item) 조회 쿼리에 `비관적 락(Pessimistic Lock)` 적용

## Results
