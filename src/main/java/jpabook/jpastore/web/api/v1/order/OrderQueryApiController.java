package jpabook.jpastore.web.api.v1.order;

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
@Tag(name = "주문 조회 v1 API", description = "주문 조회 v1 API 입니다. ** 인증된 회원 혹은 관리자 권한만 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderQueryApiController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;


    @Operation(summary = "주문 상세 정보 단건 조회 v1: findById 활용, N+1 문제 발생, '글로벌 배치' 설정으로 보완",
            description = "주문 상세 정보 단건 조회 요청입니다. ** 인증된 회원 혹은 관리자 권한만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable(name = "id") Long orderId,
                                          @AuthenticationPrincipal AuthMember authMember){
        var data = orderDtoMapper.toDto(orderService.getOrder(orderId, authMember.getUsername()));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDER, data));
    }

    // v1. 주문 리스트 조회 - OneToOne 매핑 관계 엔티티 페치조인
    @Operation(summary = "전제 주문 리스트 조회: 일대일 관계 페치조인", description = "전체 주문 리스트 조회 요청입니다. ** '관리자' 권한만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> orderList(){
        var orders = orderService.listOrder()
                .stream()
                .map(orderDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new OrderDto.ListResponse<>(orders);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }
}
