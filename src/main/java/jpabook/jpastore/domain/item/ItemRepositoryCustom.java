package jpabook.jpastore.domain.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ItemRepositoryCustom {

    Optional<Item> findItemById(Long itemId);

    Page<Item> findAll(ItemSearchCondition condition, Pageable pageable);
}
