package jpabook.jpastore.web.api.v1.membership;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.membership.MembershipService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "멤버십 등록 API", description = "멤버십 등록 API 입니다. ** '관리자' 권한만 접근 가능합니다! **")
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
@RestController
public class MembershipCommandApiController {

    private final MembershipService membershipService;


    @Operation(summary = "회원 멤버십 벌크 업데이트",
            description = "해당 분기 총 지출액에 따른 전체 회원 멤버십 벌크 업데이트 요청입니다. ** '관리자' 권한만 접근 가능합니다! **")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<?> bulkUpdateMembership() {
        membershipService.updateMembershipsByBulkUpdate();

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_MEMBERSHIP));
    }
}
