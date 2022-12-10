package jpabook.jpastore.domain.review.repository;

import jpabook.jpastore.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long>,
        ReviewRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("delete from Review r where r.item.id = :itemId")
    void bulkDeleteByItem_Id(Long itemId);
}
