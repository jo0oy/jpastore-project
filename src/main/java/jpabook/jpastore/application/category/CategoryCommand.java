package jpabook.jpastore.application.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class CategoryCommand {

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class RegisterReq {
        private String name;
        private Long parentId;
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class UpdateInfoReq {
        private String name;

        // parentId = 0 일 경우, 카테고리를 root 카테고리로 변경.
        private Long parentId;
    }
}
