package jpabook.jpastore.web.api.v1.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "주문 등록/취소 API", description = "주문 등록/취소 API 입니다. ** 인증된 회원 혹은 관리자 권한 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderCommandApiController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;


    @Operation(summary = "주문 등록", description = "새로운 주문을 생성하고 등록하는 요청입니다. ** 인증된 회원만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<?> registerOrder(@Valid @RequestBody OrderDto.OrderRegisterReq request){

        Long registeredOrderId = orderService.order(orderDtoMapper.toCommand(request));

        var data = orderDtoMapper.toDto(registeredOrderId);

        return ResponseEntity.created(URI.create("/api/v1/orders"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_ORDER, data));
    }

    @Operation(summary = "주문 취소", description = "주문 취소 요청입니다. ** 주문자 본인 또는 관리자 권한만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@Parameter(name = "id", description = "취소할 주문 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id,
                                         @AuthenticationPrincipal AuthMember authMember){

        orderService.cancelOrder(id, authMember.getUsername());

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.CANCEL_ORDER));
    }
}
