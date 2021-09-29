package jpabook.jpastore.web.api;

import jpabook.jpastore.application.ItemService;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemInfoResponseDto;
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

    @GetMapping("/api/v1/item/{id}")
    public ResponseEntity<ResultResponse> getItem(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEM, itemService.getParticularItemById(id)), HttpStatus.OK);
    }

    @GetMapping("/api/v2/item/{id}")
    public ResponseEntity<ResultResponse> getItem_V2(@PathVariable(name = "id") Long id) {
        ItemInfoResponseDto data = itemService.getParticularItemById_V2(id);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEM, data), HttpStatus.OK);
    }

    @GetMapping("/api/v2/items")
    public ResponseEntity<ResultResponse> itemsByName_V1(@RequestParam(name = "name") String name) {
        ItemListResponseDto data = itemService.findItemsByName_V1(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v3/items")
    public ResponseEntity<ResultResponse> itemsByName_V2(@RequestParam(name = "name") String name) {
        ItemListResponseDto data = itemService.findItemsByName_V2(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v2/items/ignore-case")
    public ResponseEntity<ResultResponse> itemsByNameIgnoreCase_V1(@RequestParam(name = "name") String name) {
        ItemListResponseDto data = itemService.findItemsByNameIgnoreCase_V1(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v3/items/ignore-case")
    public ResponseEntity<ResultResponse> itemsByNameIgnoreCase_V2(@RequestParam(name = "name") String name) {
        ItemListResponseDto data = itemService.findItemsByNameIgnoreCase_V2(name);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v1/items")
    public ResponseEntity<ResultResponse> items_V1() {
        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ITEMS, itemService.getItems()), HttpStatus.OK);
    }

    @PostMapping("/api/v1/item/book")
    public ResponseEntity<ResultResponse> createItem(@RequestBody BookItemSaveRequestDto requestDto) {
        itemService.saveBookItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/item/album")
    public ResponseEntity<ResultResponse> createItem(@RequestBody AlbumItemSaveRequestDto requestDto) {
        itemService.saveAlbumItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/item/dvd")
    public ResponseEntity<ResultResponse> createItem(@RequestBody DvdItemSaveRequestDto requestDto) {
        itemService.saveDvdItem(requestDto);

        return new ResponseEntity<>(ResultResponse.res(StatusCode.OK,
                ResponseMessage.CREATED_ITEM), HttpStatus.CREATED);
    }


}
