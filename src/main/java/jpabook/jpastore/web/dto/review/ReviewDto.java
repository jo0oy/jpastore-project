package jpabook.jpastore.web.dto.review;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ReviewDto {

    @ToString
    @Getter
    @Builder
    public static class RegisterReviewReq {

        @NotBlank(message = "{NotBlank.reviewBody}")
        @Length(min = 4, max = 500, message = "{Length.reviewBody}")
        private String reviewBody;

        @NotNull
        private Long memberId;

        @NotNull
        private Long itemId;
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateReviewReq {

        @NotBlank(message = "{NotBlank.reviewBody}")
        @Length(min = 4, max = 500, message = "{Length.reviewBody}")
        private String reviewBody;
    }

    @ToString
    @Getter
    @Builder
    public static class SearchCondition {
        private Long memberId;
        private String username;
        private Long itemId;
    }

    @ToString
    @Getter
    @Builder
    public static class RegisterSuccessResponse {
        Long registeredReviewId;
    }

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long reviewId;
        private MemberInfoResponse memberInfo;
        private ItemInfoResponse itemInfo;
        private String reviewBody;
    }

    @ToString
    @Getter
    @Builder
    public static class MemberInfoResponse {
        private Long memberId;
        private String username;

    }
    @ToString
    @Getter
    @Builder
    public static class ItemInfoResponse {
        private Long itemId;
        private String itemName;

    }
    @Getter
    public static class ListResponse<T> {
        private int totalCount;

        private List<T> list = new ArrayList<>();
        public ListResponse(List<T> list) {
            this.totalCount = list.size();
            this.list.addAll(list);
        }
    }
}
