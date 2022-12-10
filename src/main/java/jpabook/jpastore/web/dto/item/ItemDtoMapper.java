package jpabook.jpastore.web.dto.item;

import jpabook.jpastore.application.item.ItemCommand;
import jpabook.jpastore.application.item.ItemInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemDtoMapper {

    // DTO -> COMMAND

    ItemCommand.AlbumItemRegisterReq toCommand(ItemDto.AlbumItemRegisterReq request);

    ItemCommand.BookItemRegisterReq toCommand(ItemDto.BookItemRegisterReq request);

    ItemCommand.DvdItemRegisterReq toCommand(ItemDto.DvdItemRegisterReq request);

    ItemCommand.UpdateInfoReq toCommand(ItemDto.UpdateInfoReq request);

    ItemCommand.SearchCondition toCommand(ItemDto.SearchCondition condition);

    // INFO -> DTO

    ItemDto.RegisterSuccessResponse toDto(Long registeredItemId);

    @Mapping(target = "price", expression = "java(info.getPrice().getValue())")
    ItemDto.MainInfoResponse toDto(ItemInfo.MainInfo info);

    @Mapping(target = "price", expression = "java(info.getPrice().getValue())")
    ItemDto.DetailInfoResponse toDto(ItemInfo.DetailInfo<?> info);

    @Mapping(target = "price", expression = "java(info.getPrice().getValue())")
    ItemDto.AlbumItemInfoResponse toDto(ItemInfo.AlbumItemInfo info);

    @Mapping(target = "price", expression = "java(info.getPrice().getValue())")
    ItemDto.BookItemInfoResponse toDto(ItemInfo.BookItemInfo info);

    @Mapping(target = "price", expression = "java(info.getPrice().getValue())")
    ItemDto.DvdItemInfoResponse toDto(ItemInfo.DvdItemInfo info);
}
