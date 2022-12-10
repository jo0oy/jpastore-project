package jpabook.jpastore.application.review;

import jpabook.jpastore.domain.review.repository.ReviewSearchCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ReviewCommand {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RegisterReq {
        private String reviewBody;
        private Long memberId;
        private Long itemId;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UpdateReq {
        private String reviewBody;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class SearchCondition {
        private Long memberId;
        private String username;
        private Long itemId;

        public ReviewSearchCondition toSearchCondition() {
            return ReviewSearchCondition.builder()
                    .memberId(memberId)
                    .username(username)
                    .itemId(itemId)
                    .build();
        }
    }
}
