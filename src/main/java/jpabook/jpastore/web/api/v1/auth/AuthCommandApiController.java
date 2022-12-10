package jpabook.jpastore.web.api.v1.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.auth.AuthService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.auth.AuthDto;
import jpabook.jpastore.web.dto.auth.AuthDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "인증 API", description = "로그인, 토큰 재발행 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthCommandApiController {

    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;


    @Operation(summary = "로그인", description = "로그인 인증 요청입니다. 로그인 성공시 인증 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginReq request) {
        log.info("AuthCommandApiController post login 호출");
        var info = authService.login(authDtoMapper.toCommand(request));
        var data = authDtoMapper.toDto(info);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS, data));
    }


    @Operation(summary = "인증 토큰 재발행", description = "인증 토큰 재발행 요청입니다. 요청 성공시 갱신된 인증 토큰을 반환합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody AuthDto.ReissueReq request) {
        log.info("AuthCommandApiController post reissue 호출");
        var info = authService.reissue(authDtoMapper.toCommand(request));
        var data = authDtoMapper.toDto(info);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REISSUE_SUCCESS, data));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 요청입니다. 유효한 인증 토큰을 전달할 경우 로직이 정상 호출됩니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody AuthDto.LogoutReq request) {
        log.info("AuthCommandApiController post logout 호출");

        authService.logout(authDtoMapper.toCommand(request));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.LOGOUT_SUCCESS));
    }
}
