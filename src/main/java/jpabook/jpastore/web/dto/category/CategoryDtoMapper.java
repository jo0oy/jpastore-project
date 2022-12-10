package jpabook.jpastore.web.dto.category;

import jpabook.jpastore.application.category.CategoryCommand;
import jpabook.jpastore.application.category.CategoryInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CategoryDtoMapper {

    // DTO -> COMMAND

    @Mapping(target = "name", source = "categoryName")
    CategoryCommand.RegisterReq toCommand(CategoryDto.RegisterReq request);

    @Mapping(target = "name", source = "categoryName")
    CategoryCommand.UpdateInfoReq toCommand(CategoryDto.UpdateInfoReq request);

    // INFO -> DTO

    CategoryDto.RegisterSuccessResponse toDto(Long registeredCategoryId);

    CategoryDto.SimpleInfoResponse toDto(CategoryInfo.SimpleInfo info);

    CategoryDto.MainInfoResponse toDto(CategoryInfo.MainInfo info);

    CategoryDto.CategoryItemInfoResponse toDto(CategoryInfo.CategoryItemInfo info);

    CategoryDto.DetailWithItemsInfoResponse toDto(CategoryInfo.DetailWithItemsInfo info);

    CategoryDto.ParentChildInfoResponse toDto(CategoryInfo.ParentChildInfo info);

    CategoryDto.CategoryItemListInfoResponse toDto(CategoryInfo.CategoryItemListInfo info);

}
