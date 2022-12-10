package jpabook.jpastore.web.dto.order;

import jpabook.jpastore.application.order.OrderCommand;
import jpabook.jpastore.application.order.OrderInfo;
import jpabook.jpastore.domain.order.repository.OrderQueryInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderDtoMapper {

    // DTO -> COMMAND

    OrderCommand.OrderRegisterReq toCommand(OrderDto.OrderRegisterReq request);

    OrderCommand.OrderItemRegisterReq toCommand(OrderDto.OrderItemRegisterReq request);

    OrderCommand.OrderSearchCondition toCommand(OrderDto.OrderSearchCondition request);

    // INFO -> DTO

    OrderDto.RegisterSuccessResponse toDto(Long registeredOrderId);

    OrderDto.SimpleInfoResponse toDto(OrderInfo.SimpleInfo info);


    @Mapping(target = "totalPrice", expression = "java(info.getTotalPrice().getValue())")
    OrderDto.MainInfoResponse toDto(OrderInfo.MainInfo info);


    OrderDto.AddressInfoResponse toDto(OrderInfo.AddressInfo info);


    @Mapping(target = "orderPrice", expression = "java(info.getOrderPrice().getValue())")
    OrderDto.OrderItemInfoResponse toDto(OrderInfo.OrderItemInfo info);


    // QUERY INFO -> DTO

    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "orderStatus", expression = "java(queryInfo.getOrderStatus().getMessage())")
    @Mapping(target = "deliveryStatus", expression = "java(queryInfo.getDeliveryStatus().getMessage())")
    OrderDto.MainInfoResponse toDto(OrderQueryInfo.MainInfo queryInfo);


    @Mapping(target = "orderStatus", expression = "java(queryInfo.getOrderStatus().getMessage())")
    @Mapping(target = "deliveryStatus", expression = "java(queryInfo.getDeliveryStatus().getMessage())")
    OrderDto.SimpleInfoResponse toDto(OrderQueryInfo.SimpleInfo queryInfo);


    OrderDto.OrderItemInfoResponse toDto(OrderQueryInfo.OrderItemInfo queryInfo);


    OrderDto.AddressInfoResponse toDto(OrderQueryInfo.AddressInfo queryInfo);
}
