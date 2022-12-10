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
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.web.dto.member.MemberDto;
import jpabook.jpastore.web.dto.member.MemberDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "회원 조회 API", description = "회원 조회 API 입니다. ** 인증된 회원 혹은 '관리자' 권한만 요청 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberQueryApiController {

    private final MemberService memberService;
    private final MemberDtoMapper memberDtoMapper;


    @Operation(summary = "인증된 회원 본인 정보 조회", description = "현재 로그인되어 있는 회원 본인의 상세 정보 조회 요청입니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/my-info")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal AuthMember authMember) {
        var data = memberDtoMapper.toDto(memberService.getMember(authMember.getUsername()));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_MEMBER, data));
    }


    @Operation(summary = "회원 상세 정보 조회 by 회원 id", description = "회원 id를 통한 회원 상세 정보 조회 요청입니다. **회원 '본인' 혹은 '관리자' 권한만 접근 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findMemberById(@Parameter(name = "id", description = "조회할 회원 id", in = ParameterIn.PATH, required = true) @PathVariable(name = "id") Long id,
                                            @AuthenticationPrincipal AuthMember authMember) {
        var data = memberDtoMapper.toDto(memberService.getMember(id, authMember.getUsername()));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_MEMBER, data));
    }


    @Operation(summary = "전체 회원 리스트 조회", description = "전체 회원 리스트 조회 요청입니다. **'관리자' 권한 접근 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> memberList(@Parameter(name = "username", description = "회원 로그인 ID(username)", in = ParameterIn.QUERY) @RequestParam(name = "username", required = false) String username,
                                        @Parameter(name = "grade", description = "회원 등급", in = ParameterIn.QUERY) @RequestParam(name = "grade", required = false) String grade) {

        var members = memberService.listMembers(username, (StringUtils.hasText(grade)) ? Grade.valueOf(grade.toUpperCase()) : null)
                .stream().map(memberDtoMapper::toDto).collect(Collectors.toList());

        var data = new MemberDto.ListResponse<>(members);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_MEMBER, data));
    }


    @Operation(summary = "전체 회원 리스트 조회 (페이징, 정렬, 검색 기능 포함)", description = "페이징, 정렬, 검색(로그인 ID/회원 등급) 기능을 포함한 회원 리스트 조회 요청입니다. **'관리자' 권한 접근 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> members(@Parameter(name = "username", description = "회원 로그인 ID(username)", in = ParameterIn.QUERY) @RequestParam(name = "username", required = false) String username,
                                     @Parameter(name = "grade", description = "회원 등급", in = ParameterIn.QUERY) @RequestParam(name = "grade", required = false) String grade,
                                     @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        var data = memberService.members(username, (StringUtils.hasText(grade)) ? Grade.valueOf(grade.toUpperCase()) : null, pageable)
                .map(memberDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_MEMBER, data));
    }
}
