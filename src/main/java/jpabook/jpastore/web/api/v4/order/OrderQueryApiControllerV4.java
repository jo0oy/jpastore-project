package jpabook.jpastore.web.api.v4.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.order.OrderService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.web.dto.order.OrderDto;
import jpabook.jpastore.web.dto.order.OrderDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "주문 조회 v4 API", description = "주문 조회 v4 API 입니다. ** 관리자 권한만 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v4")
@RestController
public class OrderQueryApiControllerV4 {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    /**
     *  JPA에 DTO 바로 조회.
     */
    @Operation(summary = "전체 주문 간단 정보 리스트 조회: Querydsl을 사용해 DTO로 바로 조회",
            description = "전체 주문의 간단한 정보 리스트 요청입니다. Querydsl로 작성된 쿼리에서 DTO로 필요한 정보만 바로 조회합니다. " +
                    " ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/simple-orders")
    public ResponseEntity<?> simpleOrderList(){
        var orders
                = orderService.listOrderSimpleInfos()
                .stream()
                .map(orderDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new OrderDto.ListResponse<>(orders);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    @Operation(summary = "전체 주문 리스트 조회(페이징, 정렬, 검색 기능 포함)",
            description = "페이징, 정렬, 검색(주문자 ID/주문자 이름/주문 ID/주문 상태/배달 상태/주문 생성일) 기능이 포함된 주문 리스트 조회 요청입니다." +
                    " ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<?> ordersByCondition(@RequestParam(name = "memberId", required = false) Long memberId,
                                               @RequestParam(name = "memberName", required = false) String memberName,
                                               @RequestParam(name = "orderId", required = false) Long orderId,
                                               @RequestParam(name = "status", required = false) String status,
                                               @RequestParam(name = "deliveryStatus", required = false) String deliveryStatus,
                                               @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        var condition = OrderDto.OrderSearchCondition.builder()
                .memberId(memberId)
                .memberName(memberName)
                .orderId(orderId)
                .status((StringUtils.hasText(status) ? OrderStatus.valueOf(status.toUpperCase()) : null))
                .deliveryStatus((StringUtils.hasText(deliveryStatus) ? DeliveryStatus.valueOf(status.toUpperCase()) : null))
                .build();

        var data
                = orderService.listOrder(orderDtoMapper.toCommand(condition), pageable).map(orderDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }
}
