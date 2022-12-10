package jpabook.jpastore.application.review;

import jpabook.jpastore.domain.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ReviewInfo {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long reviewId;
        private MemberInfo memberInfo;
        private ItemInfo itemInfo;
        private String reviewBody;

        public MainInfo(Review entity) {
            this.reviewId = entity.getId();
            this.memberInfo = MemberInfo.builder()
                    .memberId(entity.getMember().getId())
                    .username(entity.getMember().getUsername())
                    .build();

            this.itemInfo = ItemInfo.builder()
                    .itemId(entity.getItem().getId())
                    .itemName(entity.getItem().getName())
                    .build();

            this.reviewBody = entity.getReviewBody();
        }
    }

    @ToString
    @Getter
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String username;
    }

    @ToString
    @Getter
    @Builder
    public static class ItemInfo {
        private Long itemId;
        private String itemName;
    }
}
