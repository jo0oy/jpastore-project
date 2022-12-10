package jpabook.jpastore.application.item;

import jpabook.jpastore.domain.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemService {

    @Transactional
    Long saveBookItem(ItemCommand.BookItemRegisterReq command);

    @Transactional
    Long saveAlbumItem(ItemCommand.AlbumItemRegisterReq command);

    @Transactional
    Long saveDvdItem(ItemCommand.DvdItemRegisterReq command);

    ItemInfo.MainInfo getItem(Long id);

    <T> T itemDetail_V1(Long id);

    ItemInfo.DetailInfo<Item> itemDetail_V2(Long id);

    // Query Method 사용 버전 : case insensitive
    List<ItemInfo.MainInfo> searchItemsByName_V1(String name);

    // JPQL 사용 버전 : case insensitive
    List<ItemInfo.MainInfo> searchItemsByName_V2(String name);

    Page<ItemInfo.MainInfo> items(ItemCommand.SearchCondition condition, Pageable pageable);

    List<ItemInfo.MainInfo> itemList();

    @Transactional
    void updateItemInfo(Long id, ItemCommand.UpdateInfoReq command);

    @Transactional
    void delete(Long id);
}
