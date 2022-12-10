package jpabook.jpastore.domain.review.repository;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewSearchCondition {
    private Long memberId;
    private String username;
    private Long itemId;

    @Builder
    public ReviewSearchCondition(Long memberId,
                                 String username,
                                 Long itemId) {
        this.memberId = memberId;
        this.username = username;
        this.itemId = itemId;
    }
}
