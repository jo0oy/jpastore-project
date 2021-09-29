package jpabook.jpastore.application;

import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void 회원가입_테스트() throws Exception {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        // when
        Long savedId = memberService.join(requestDto);
        Member member = memberRepository.findById(savedId).orElse(null);

        // then
        assertThat(savedId).isEqualTo(1L);
        assertThat(member.getName()).isEqualTo("kim");
    }

    @Test
    public void 중복_이름_예외_테스트() throws Exception {
        // given
        MemberSaveRequestDto requestDto1 = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        MemberSaveRequestDto requestDto2 = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        // when
        Long savedId1 = memberService.join(requestDto1);

        Exception exception = assertThrows(IllegalStateException.class, () -> {memberService.join(requestDto2);});

        // then
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 회원 이름입니다.");
    }
}