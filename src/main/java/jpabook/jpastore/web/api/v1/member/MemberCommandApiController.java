package jpabook.jpastore.web.api.v1.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.member.MemberService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.domain.auth.AuthMember;
import jpabook.jpastore.web.dto.member.MemberDto;
import jpabook.jpastore.web.dto.member.MemberDtoMapper;
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
@Tag(name = "회원 등록/수정 API", description = "회원 등록/수정 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberCommandApiController {

    private final MemberService memberService;
    private final MemberDtoMapper memberDtoMapper;


    @Operation(summary = "회원 등록", description = "회원 등록 요청입니다.")
    @PostMapping("")
    public ResponseEntity<?> registerMember(@Valid @RequestBody MemberDto.RegisterReq request) {
        log.info("member Register requestDto={}", request);

        Long registeredMemberId = memberService.join(memberDtoMapper.toCommand(request));
        var data = memberDtoMapper.toDto(registeredMemberId);

        return ResponseEntity.created(URI.create("/api/v1/members"))
                .body(data);
    }


    @Operation(summary = "회원 정보 수정", description = "로그인된 회원 정보 수정 요청입니다. 인증된 회원 '본인'만 요청 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN') and (#request.username == principal.username)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMemberInfo(@Parameter(name = "id", description = "수정할 회원 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id,
                                              @Valid @RequestBody MemberDto.UpdateInfoReq request) {
        memberService.updateMemberInfo(id, memberDtoMapper.toCommand(request));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_MEMBER));
    }

    @Operation(summary = "회원 삭제", description = "회원 삭제 요청입니다. 인증된 회원 '본인' 또는 관리자만 요청 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Parameter(name = "id", description = "삭제할 회원 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id,
                                    @AuthenticationPrincipal AuthMember authMember) {

        memberService.delete(id, authMember.getUsername());

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.DELETE_MEMBER));
    }
}
