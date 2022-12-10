package jpabook.jpastore.domain.review;

import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false)
    private String reviewBody;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, updatable = false)
    private Item item;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @Builder
    public Review(String reviewBody,
                  Member member,
                  Item item) {

        if(!(StringUtils.hasText(reviewBody) && reviewBody.length() >= 4 && reviewBody.length() <= 400))
            throw new IllegalArgumentException("Invalid Param. reviewBody");
        if(Objects.isNull(member)) throw new IllegalArgumentException("Invalid Param. member");
        if(Objects.isNull(item)) throw new IllegalArgumentException("Invalid Param. item");

        this.reviewBody = reviewBody;
        this.member = member;
        this.isDeleted = false;

        // review - item ( N : 1 양방향 관계) setting
        this.setItem(item);
    }

    //== 연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
    }

    public void setItem(Item item) {
        this.item = item;
        item.addReview(this);
    }

    //== 생성 메서드 ==//
    public static Review createReview(String reviewBody, Member member, Item item) {

        return Review.builder()
                .reviewBody(reviewBody)
                .member(member)
                .item(item)
                .build();
    }

    // 비즈니스 로직
    public void updateReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }

    public boolean hasAuthorityToUpdate(Member member) {
        return this.member.equals(member);
    }

    public boolean hasAuthorityToDelete(Member member) {
        return this.member.equals(member) || member.getRole() == Role.ADMIN;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
