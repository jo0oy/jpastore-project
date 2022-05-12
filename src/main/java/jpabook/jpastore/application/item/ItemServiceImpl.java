package jpabook.jpastore.application.item;

import jpabook.jpastore.application.dto.item.*;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.domain.item.*;
import jpabook.jpastore.dto.item.AlbumItemSaveRequestDto;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import jpabook.jpastore.dto.item.DvdItemSaveRequestDto;
import jpabook.jpastore.exception.CategoryNotFoundException;
import jpabook.jpastore.exception.ItemNotFoundException;
import jpabook.jpastore.dto.item.ItemUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookRepository bookRepository;
    private final AlbumRepository albumRepository;
    private final DvdRepository dvdRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 상품 등록
     */
    @Override
    @Transactional
    public Long saveBookItem(BookItemSaveRequestDto requestDto) {
        log.info("creating book item....");
        Book createdItem = bookRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    @Override
    @Transactional
    public Long saveAlbumItem(AlbumItemSaveRequestDto requestDto) {
        log.info("creating album item....");
        Album createdItem = albumRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    @Override
    @Transactional
    public Long saveDvdItem(DvdItemSaveRequestDto requestDto) {
        log.info("creating dvd item....");
        Dvd createdItem = dvdRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    /**
     * 단일 상품 조회
     */
    @Override
    public ItemResponseDto getItem(Long id) {
        log.info("get item simple info by id={}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다." + id));

        return new ItemResponseDto(item);
    }

    @Override
    public <T> T itemDetail_V1(Long id) {
        log.info("get item detail by id={}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다." + id));

        if (item instanceof Book) {
            log.info("item is book type");
            return (T) new BookItemResponseDto((Book) item);
        } else if (item instanceof Album) {
            log.info("item is album type");
            return (T) new AlbumItemResponseDto((Album) item);
        } else if (item instanceof Dvd) {
            log.info("item is dvd type");
            return (T) new DvdItemResponseDto((Dvd) item);
        }

        return (T) new ItemDetailResponseDto<>(item);
    }

    @Override
    public ItemDetailResponseDto<Item> itemDetail_V2(Long id) {
        log.info("get item detail by id={}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다." + id));

        return new ItemDetailResponseDto<>(item);
    }

    /**
     * 상품명으로 상품 리스트 검색
     */
    // Query Method 사용 버전 : case insensitive
    @Override
    public ItemListResponseDto<ItemResponseDto> searchItemsByName_V1(String name) {
        log.info("search item list by name={}", name);
        return new ItemListResponseDto<>(
                itemRepository.findByNameContainingIgnoreCase(name)
                        .stream()
                        .map(ItemResponseDto::new)
                        .collect(Collectors.toList())
        );
    }

    // JPQL 사용 버전 : case insensitive
    @Override
    public ItemListResponseDto<ItemResponseDto> searchItemsByName_V2(String name) {
        log.info("search item list by name={}", name);
        return new ItemListResponseDto<>(
                itemRepository.searchItemsByNameIgnoreCase(name)
                        .stream()
                        .map(ItemResponseDto::new)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 상품 전제 조회
     */
    @Override
    public ItemListResponseDto<ItemResponseDto> itemList() {
        log.info("get item list...");

        return new ItemListResponseDto<>(itemRepository.findAll()
                .stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList()));
    }

    /**
     * 상품 정보 업데이트
     */
    @Override
    @Transactional
    public void updateItemInfo(Long id, ItemUpdateRequestDto requestDto) {
        log.info("updating item id={}", id);

        // 상품 엔티티 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다." + id));

        // item 공통 부분 업데이트
        item.updateItem(requestDto.getName(), requestDto.getPrice(), requestDto.getStockQuantity());

        // 상품 타입에 따른 업데이트
        if (item instanceof Book) {
            ((Book) item).updateBook(requestDto.getAuthor(), requestDto.getIsbn());
        } else if (item instanceof Album) {
            ((Album) item).updateAlbum(requestDto.getArtist(), requestDto.getEtc());
        } else if (item instanceof Dvd) {
            ((Dvd) item).updateDvd(requestDto.getDirector(), requestDto.getActor());
        }
    }

    /**
     * 상품 삭제
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("deleting item id={}", id);

        // 싱품 엔티티 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다." + id));

        // 카테고리-상품 연관관계 정보 삭제
        categoryRepository.deleteCategoryItemByItemId(id);

        // 상품 삭제
        itemRepository.delete(item);
    }

    // 상품 - 카테고리 설정 메소드
    private void setCategoryItem(Long categoryId, Item item) {
        log.info("setting category-item...");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리 입니다. id = " + categoryId));

        CategoryItem.createCategoryItem(item, category);
    }
}
