package jpabook.jpastore.security;

import jpabook.jpastore.domain.auth.AuthMember;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> {
                    log.error("존재하지 않는 회원입니다. username={}", username);
                    throw new UsernameNotFoundException("존재하지 않는 회원입니다. username=" + username);
                }
        );

        log.info("회원 로딩 by username {}", username);

        return new AuthMember(member);
    }
}
