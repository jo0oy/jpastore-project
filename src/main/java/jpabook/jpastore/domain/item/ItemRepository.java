package jpabook.jpastore.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByNameIgnoreCase(String name);

    List<Item> findByNameContainingIgnoreCase(String name);

    @Query("select i from Item i where i.name like %:name%")
    List<Item> searchItemsByName(@Param(value = "name") String name);

    @Query("select i from Item i where lower(i.name) like lower(concat('%',:name,'%'))")
    List<Item> searchItemsByNameIgnoreCase(@Param(value = "name") String name);
}
