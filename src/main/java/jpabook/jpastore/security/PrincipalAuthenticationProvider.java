package jpabook.jpastore.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class PrincipalAuthenticationProvider implements AuthenticationProvider {

    private final PrincipalUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("PrincipalAuthenticationProvider.authenticate");
        var username = authentication.getName();
        var password = String.valueOf(authentication.getCredentials());

        var userDetail = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetail.getPassword())) {
            throw new BadCredentialsException("비밀번호가 올바르지 않습니다.");
        }

        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(),
                null, userDetail.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
