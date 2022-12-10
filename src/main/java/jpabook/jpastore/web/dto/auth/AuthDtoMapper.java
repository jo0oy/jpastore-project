package jpabook.jpastore.web.dto.auth;

import jpabook.jpastore.domain.auth.AuthCommand;
import jpabook.jpastore.domain.auth.AuthInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthDtoMapper {

    // DTO -> COMMAND

    AuthCommand.LoginReq toCommand(AuthDto.LoginReq request);

    AuthCommand.ReissueReq toCommand(AuthDto.ReissueReq request);

    AuthCommand.LogoutReq toCommand(AuthDto.LogoutReq request);

    // INFO -> DTO

    AuthDto.TokenInfoResponse toDto(AuthInfo.TokenInfo info);

    AuthDto.AccessTokenResponse toDto(AuthInfo.AccessToken info);
}
