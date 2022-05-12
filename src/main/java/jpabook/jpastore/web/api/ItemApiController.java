package jpabook.jpastore.web.api;

import jpabook.jpastore.application.dto.item.ItemDetailResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.application.item.ItemService;
import jpabook.jpastore.domain.item.Item;
import jpabook.jpastore.dto.item.AlbumItemSaveRequestDto;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import jpabook.jpastore.dto.item.DvdItemSaveRequestDto;
import jpabook.jpastore.web.response.ResponseMessage;
import jpabook.jpastore.web.response.ResultResponse;
import jpabook.jpastore.web.response.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ItemApiController {

    private final ItemService itemService;

    @PostMapping("/api/v1/item/book")
    public ResponseEntity<ResultResponse<?>> createItem(@RequestBody BookItemSaveRequestDto requestDto) {
        itemService.saveBookItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/item/album")
    public ResponseEntity<ResultResponse<?>> createItem(@RequestBody AlbumItemSaveRequestDto requestDto) {
        itemService.saveAlbumItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/item/dvd")
    public ResponseEntity<ResultResponse<?>> createItem(@RequestBody DvdItemSaveRequestDto requestDto) {
        itemService.saveDvdItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/item/{id}")
    public ResponseEntity<ResultResponse<?>> getItem(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEM, itemService.itemDetail_V1(id)), HttpStatus.OK);
    }

    @GetMapping("/api/v2/item/{id}")
    public ResponseEntity<ResultResponse<?>> getItem_V2(@PathVariable(name = "id") Long id) {
        ItemDetailResponseDto<Item> data = itemService.itemDetail_V2(id);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEM, data), HttpStatus.OK);
    }

    @GetMapping("/api/v1/items")
    public ResponseEntity<ResultResponse<?>> searchItemsByName_V1(@RequestParam(name = "name") String name) {
        ItemListResponseDto<ItemResponseDto> data = itemService.searchItemsByName_V1(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v2/items")
    public ResponseEntity<ResultResponse<?>> searchItemsByName_V2(@RequestParam(name = "name") String name) {
        ItemListResponseDto<ItemResponseDto> data = itemService.searchItemsByName_V2(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v1/items/list")
    public ResponseEntity<ResultResponse<?>> items_V1() {
        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, itemService.itemList()), HttpStatus.OK);
    }


}
