package jpabook.jpastore.web.dto.member;

import jpabook.jpastore.application.member.MemberCommand;
import jpabook.jpastore.application.member.MemberInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberDtoMapper {

    // DTO -> COMMAND

    MemberCommand.AddressInfo toCommand(MemberDto.AddressInfo request);

    MemberCommand.RegisterReq toCommand(MemberDto.RegisterReq request);

    MemberCommand.UpdateInfoReq toCommand(MemberDto.UpdateInfoReq request);

    // INFO -> DTO

    MemberDto.RegisterSuccessResponse toDto(Long registeredMemberId);

    MemberDto.AddressInfoResponse toDto(MemberInfo.AddressInfo info);

    @Mapping(target = "totalSpending", expression = "java(info.getTotalSpending().getValue())")
    MemberDto.MembershipInfoResponse toDto(MemberInfo.MembershipInfo info);

    MemberDto.MainInfoResponse toDto(MemberInfo.MainInfo info);
}
