package jpabook.jpastore.application.item;

import jpabook.jpastore.application.dto.item.ItemDetailResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.dto.item.AlbumItemSaveRequestDto;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import jpabook.jpastore.dto.item.DvdItemSaveRequestDto;
import jpabook.jpastore.dto.item.ItemUpdateRequestDto;

public interface ItemService {

    Long saveBookItem(BookItemSaveRequestDto requestDto);

    Long saveAlbumItem(AlbumItemSaveRequestDto requestDto);

    Long saveDvdItem(DvdItemSaveRequestDto requestDto);

    ItemResponseDto getItem(Long id);

    <T> T itemDetail_V1(Long id);

    ItemDetailResponseDto<Item> itemDetail_V2(Long id);

    // Query Method 사용 버전 : case insensitive
    ItemListResponseDto<ItemResponseDto> searchItemsByName_V1(String name);

    // JPQL 사용 버전 : case insensitive
    ItemListResponseDto<ItemResponseDto> searchItemsByName_V2(String name);

    ItemListResponseDto<ItemResponseDto> itemList();

    void updateItemInfo(Long id, ItemUpdateRequestDto requestDto);

    void delete(Long id);
}
