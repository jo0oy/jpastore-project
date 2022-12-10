package jpabook.jpastore.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    List<Item> findByNameContainingIgnoreCase(String name);

    @Query("select i from Item i where lower(i.name) like lower(concat('%',:name,'%')) and i.isDeleted = false")
    List<Item> searchItemsByNameIgnoreCase(@Param(value = "name") String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :itemId and i.isDeleted = false")
    Optional<Item> findItemForUpdate(@Param("itemId") Long itemId);
}
