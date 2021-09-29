package jpabook.jpastore.web.api;

import jpabook.jpastore.application.MemberService;
import jpabook.jpastore.application.dto.member.MemberListResponseDto;
import jpabook.jpastore.application.dto.member.MemberResponseDto;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import jpabook.jpastore.dto.member.MemberUpdateRequestDto;
import jpabook.jpastore.web.response.ResultResponse;
import jpabook.jpastore.web.response.ResponseMessage;
import jpabook.jpastore.web.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/member")
    public ResponseEntity createMember(@RequestBody MemberSaveRequestDto requestDto) {

        Long createdMemberId = memberService.join(requestDto);
        Map<String, Long> data = new HashMap<>();
        data.put("created_member_id", createdMemberId);

        return ResponseEntity.created(URI.create("/api/v1/member"))
                .body(data);
    }

    @GetMapping("/api/v1/member/{id}")
    public ResponseEntity findMemberById(@PathVariable(name = "id") Long id) {
        MemberResponseDto data = memberService.findById(id);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_MEMBER, data));
    }

    @GetMapping("/api/v1/members")
    public ResponseEntity findAllMembers() {
        MemberListResponseDto data = memberService.findMembers();

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_MEMBER, data));
    }

    @PutMapping("/api/v1/member/{id}")
    public ResponseEntity updateMemberInfo(@PathVariable(name = "id") Long id,
                                           @RequestBody MemberUpdateRequestDto requestDto) {
        memberService.updateMemberInfo(id, requestDto);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK,
                ResponseMessage.UPDATED_MEMBER));
    }
}
