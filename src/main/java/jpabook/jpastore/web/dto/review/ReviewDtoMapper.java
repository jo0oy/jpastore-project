package jpabook.jpastore.web.dto.review;

import jpabook.jpastore.application.review.ReviewCommand;
import jpabook.jpastore.application.review.ReviewInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReviewDtoMapper {

    // DTO -> COMMAND

    ReviewCommand.RegisterReq toCommand(ReviewDto.RegisterReviewReq request);

    ReviewCommand.UpdateReq toCommand(ReviewDto.UpdateReviewReq request);

    ReviewCommand.SearchCondition toCommand(ReviewDto.SearchCondition request);


    // INFO -> DTO

    ReviewDto.RegisterSuccessResponse toDto(Long registeredReviewId);

    ReviewDto.MainInfoResponse toDto(ReviewInfo.MainInfo info);

    ReviewDto.MemberInfoResponse toDto(ReviewInfo.MemberInfo info);

    ReviewDto.ItemInfoResponse toDto(ReviewInfo.ItemInfo info);
}
