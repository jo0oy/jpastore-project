package jpabook.jpastore.web.api.v3.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.order.OrderService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.order.OrderDto;
import jpabook.jpastore.web.dto.order.OrderDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "주문 조회 v3 API", description = "주문 조회 v3 API 입니다. ** 관리자 권한만 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v3/orders")
@RestController
public class OrderQueryApiControllerV3 {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @Operation(summary = "전체 주문 리스트 조회 with 컬렉션(orderItems): 'distinct' 키워드 작성함",
            description = "전체 주문 리스트 요청입니다. 모든 연관관계 엔티티를 페치조인으로 조회합니다. " +
                    "'distinct' 키워드를 작성했고, 모든 조회 결과를 애플리케이션에 가져와 distinct 처리해 반환합니다." +
                    " ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/distinct")
    public ResponseEntity<?> orderListWithOrderItemsDistinct() {
        var list
                = orderService.listOrderFetchOrderItemsDistinct()
                .stream()
                .map(orderDtoMapper::toDto)
                .collect(Collectors.toList());

        var data = new OrderDto.ListResponse<>(list);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    @Operation(summary = "전체 주문 리스트 조회 (페이징 기능)",
            description = "전체 주문 리스트 페이징 조회 요청입니다. " +
                    "일대일 매핑 엔티티(Member, Delivery)를 페치조인으로 조회하고, 컬렉션은 글로벌 배치를 통해 조회됩니다." +
                    " ** 관리자 계정만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> orderListPaging(@PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        var data = orderService.listOrder(pageable).map(orderDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }
}
