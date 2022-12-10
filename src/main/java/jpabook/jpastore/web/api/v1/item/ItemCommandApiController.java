package jpabook.jpastore.web.api.v1.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpastore.application.item.ItemService;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.web.dto.item.ItemDto;
import jpabook.jpastore.web.dto.item.ItemDtoMapper;
import jpabook.jpastore.web.validator.ItemUpdateRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

@Slf4j
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
        @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
})
@Tag(name = "상품 등록/수정 API", description = "상품 등록/수정 API 입니다. ** '관리자' 권한만 요청 가능합니다! **")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() and  hasRole('ROLE_ADMIN')") // 인증된 관리자만 상품 등록 가능
@RequestMapping("/api/v1/items")
@RestController
public class ItemCommandApiController {

    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;
    private final ItemUpdateRequestValidator updateRequestValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        log.info("binder target is not null : {}, target Class : {}", Objects.nonNull(binder.getTarget()), binder.getTarget());
        if (binder.getTarget() != null && ItemDto.UpdateInfoReq.class.equals(binder.getTarget().getClass())) {
            binder.addValidators(updateRequestValidator); // 상품 수정 데이터 검증기 설정
        }
    }

    @Operation(summary = "책 상품 등록", description = "책 상품 등록 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PostMapping("/book")
    public ResponseEntity<?> registerItem(@Valid @RequestBody ItemDto.BookItemRegisterReq registerReq) {
        var registeredItemId = itemService.saveBookItem(itemDtoMapper.toCommand(registerReq));

        var data = itemDtoMapper.toDto(registeredItemId);

        return ResponseEntity.created(URI.create("/api/v1/items/book"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_ITEM, data));
    }


    @Operation(summary = "앨범 상품 등록", description = "앨범 상품 등록 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PostMapping("/album")
    public ResponseEntity<?> registerItem(@Valid @RequestBody ItemDto.AlbumItemRegisterReq registerReq) {
        var registeredItemId = itemService.saveAlbumItem(itemDtoMapper.toCommand(registerReq));

        var data = itemDtoMapper.toDto(registeredItemId);

        return ResponseEntity.created(URI.create("/api/v1/items/album"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_ITEM, data));
    }


    @Operation(summary = "DVD 상품 등록", description = "DVD 상품 등록 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PostMapping("/dvd")
    public ResponseEntity<?> registerItem(@Valid @RequestBody ItemDto.DvdItemRegisterReq registerReq) {
        var registeredItemId = itemService.saveDvdItem(itemDtoMapper.toCommand(registerReq));

        var data = itemDtoMapper.toDto(registeredItemId);

        return ResponseEntity.created(URI.create("/api/v1/items/dvd"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.REGISTER_ITEM, data));
    }


    @Operation(summary = "상품 정보 수정", description = "상품 정보 수정 요청입니다. 관리자 권한만 접근 가능합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItemInfo(@Parameter(name = "id", description = "수정할 상품 id", in = ParameterIn.PATH, required = true) @PathVariable("id") Long id,
                                            @Valid @RequestBody ItemDto.UpdateInfoReq updateReq) {
        itemService.updateItemInfo(id, itemDtoMapper.toCommand(updateReq));

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_ITEM));
    }

    @Operation(summary = "상품 삭제", description = "상품 삭제 요청입니다. 관리자 권한만 접근 가능합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Parameter(name = "id", description = "삭제할 상품 id", in = ParameterIn.PATH, required = true) @PathVariable("id") Long id) {

        itemService.delete(id);

        return ResponseEntity.ok()
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.DELETE_ITEM));
    }
}
