package jpabook.jpastore.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "일반 회원");

    private final String value;
    private final String desc;
}
