package jpabook.jpastore.application;

import jpabook.jpastore.application.dto.item.*;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.domain.item.*;
import jpabook.jpastore.dto.item.AlbumItemSaveRequestDto;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import jpabook.jpastore.dto.item.DvdItemSaveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService<T> {

    private final ItemRepository itemRepository;
    private final BookRepository bookRepository;
    private final AlbumRepository albumRepository;
    private final DvdRepository dvdRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 상품 등록
     */
    @Transactional
    public void saveBookItem(BookItemSaveRequestDto requestDto) {
        log.info("creating book item....");
        Book createdItem = bookRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);
    }

    @Transactional
    public void saveAlbumItem(AlbumItemSaveRequestDto requestDto) {
        log.info("creating album item....");
        Album createdItem = albumRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);
    }

    @Transactional
    public void saveDvdItem(DvdItemSaveRequestDto requestDto) {
        log.info("creating dvd item....");
        Dvd createdItem = dvdRepository.save(requestDto.toEntity());

        setCategoryItem(requestDto.getCategoryId(), createdItem);
    }

    /**
     * 단일 상품 조회
     */
    public ItemResponseDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다." + id));

        return new ItemResponseDto(item);
    }


//    public List<ItemResponseDto> findItems() {
//        List<Item> items = itemRepository.findAll();
//
//        return items.stream()
//                .map(ItemResponseDto::new)
//                .collect(Collectors.toList());
//    }

    public T getParticularItemById_V1(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다." + id));

        if (item instanceof Book) {
            return (T) new BookItemResponseDto((Book) item);
        } else if (item instanceof Album) {
            return (T) new AlbumItemResponseDto((Album) item);
        } else if (item instanceof Dvd) {
            return (T) new DvdItemResponseDto((Dvd) item);
        }

        return (T) new ItemInfoResponseDto<>(item);
    }

    public ItemInfoResponseDto<Item> getParticularItemById_V2(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다." + id));

        return new ItemInfoResponseDto<>(item);
    }

    /**
     * 상품명으로 상품 검색
     */
    // Query Method 사용 버전 : case sensitive
    public ItemListResponseDto findItemsByName_V1(String name) {
        return new ItemListResponseDto(itemRepository.findByNameIgnoreCase(name).stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList()));
    }

    // Query Method 사용 버전 : case insensitive(ignoring case)
    public ItemListResponseDto findItemsByNameIgnoreCase_V1(String name) {
        return new ItemListResponseDto(
                itemRepository.findByNameContainingIgnoreCase(name).stream()
                        .map(ItemResponseDto::new)
                        .collect(Collectors.toList()));
    }

    // JPQl 사용 버전 : case sensitive
    public ItemListResponseDto findItemsByName_V2(String name) {
        return new ItemListResponseDto(itemRepository.searchItemsByName(name).stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList()));
    }

    // JPQl 사용 버전 : case insensitive(ignoring case)
    public ItemListResponseDto findItemsByNameIgnoreCase_V2(String name) {
        return new ItemListResponseDto(itemRepository.searchItemsByNameIgnoreCase(name).stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList()));
    }

    /**
     * 상품 전제 조회
     */
    public ItemListResponseDto getItems() {
        return new ItemListResponseDto(itemRepository.findAll()
                .stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList()));
    }

    // 상품 - 카테고리 설정 메소드
    private void setCategoryItem(Long categoryId, Item item) {
        log.info("setting category-item...");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다. id = " + categoryId));

        CategoryItem ci = CategoryItem.createCategoryItem(item, category);
        category.addCategoryItem(ci);
    }
}
