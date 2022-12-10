package jpabook.jpastore.web.api.v2.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.order.OrderService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.domain.auth.AuthMember;
import jpabook.jpastore.web.dto.order.OrderDto;
import jpabook.jpastore.web.dto.order.OrderDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "주문 조회 v2 API", description = "주문 조회 v2 API 입니다. ** 인증된 회원 혹은 관리자 권한만 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class OrderQueryApiControllerV2 {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    /**
     *  Member, Delivery 정보 포함한 단일 주문 간단 조회.
     *  페치조인을 통한 조회.
     */
    @Operation(summary = "주문 간단 정보 단건 조회: 일대일 매핑 관계(회원, 배달) 페치 조인",
            description = "단일 주문의 간단한 정보 조회 요청입니다. ** 주문자 '본인' 혹은 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/simple-orders/{id}")
    public ResponseEntity<?> getSimpleOrderWithMemberDelivery(@PathVariable(name = "id") Long orderId,
                                                              @AuthenticationPrincipal AuthMember authMember){
        var simpleOrder = orderService.getOrderSimpleInfo(orderId, authMember.getUsername());
        var data = orderDtoMapper.toDto(simpleOrder);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDER, data));
    }

    /**
     *  Member, Delivery 정보 포함한 단일 주문 상세 조회.
     *  페치조인을 통한 조회.
     */
    @Operation(summary = "주문 상세 정보 단건 조회: 일대일 매핑 관계(Member, Delivery) 페치 조인 + 주문 상픔은 글로벌 배치 설정으로 조회",
            description = "단일 주문의 상세 정보 조회 요청입니다. ** 주문자 '본인' 혹은 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderWithMemberDelivery(@PathVariable(name = "id") Long orderId,
                                                        @AuthenticationPrincipal AuthMember authMember){
        var order = orderService.getOrderFetch(orderId, authMember.getUsername());
        var data = orderDtoMapper.toDto(order);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDER, data));
    }

    /**
     *  Member, Delivery 정보 포함한 simple 전체 주문 조회. -> service 에서 info 로 변환 작업.
     *  페치조인을 통한 조회.
     */
    @Operation(summary = "전체 주문 간단 정보 리스트 조회: 일대일 매핑 관계(Member, Delivery) 페치 조인",
            description = "전체 주문의 간단한 정보(주문자, 주문/배달 상태)를 조회하는 요청입니다. ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/simple-orders")
    public ResponseEntity<?> listSimpleOrder(@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        var data = orderService.listSimpleOrder(pageable).map(orderDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    /**
     *  전체 주문 조회 with 컬렉션(orderItems) : 페치조인 사용. - no 'distinct'
     */
    @Operation(summary = "전체 주문 리스트 조회 with 컬렉션(orderItems): 일대일(Member, Delivery)/일대다 컬렉션(OrderItems) 페치 조인, no 'distinct'",
            description = "전체 주문 리스트 요청입니다. 모든 연관관계 엔티티를 페치조인으로 조회합니다. " +
                    "'distinct' 키워드를 작성하지 않아 카타시안 곱의 결과값을 반환합니다. ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<?> orderListWithOrderItems() {
        var list
                = orderService.listOrderFetchOrderItems()
                .stream()
                .map(orderDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new OrderDto.ListResponse<>(list);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }
}
