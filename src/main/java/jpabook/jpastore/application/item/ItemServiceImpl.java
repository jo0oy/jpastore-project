package jpabook.jpastore.application.item;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.common.utils.PageRequestUtils;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.domain.item.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Long saveBookItem(ItemCommand.BookItemRegisterReq command) {
        log.info("creating book item....");
        Book createdItem = bookRepository.save(command.toEntity());

        setCategoryItem(command.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    @Override
    @Transactional
    public Long saveAlbumItem(ItemCommand.AlbumItemRegisterReq command) {
        log.info("creating album item....");
        Album createdItem = albumRepository.save(command.toEntity());

        setCategoryItem(command.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    @Override
    @Transactional
    public Long saveDvdItem(ItemCommand.DvdItemRegisterReq command) {
        log.info("creating dvd item....");
        Dvd createdItem = dvdRepository.save(command.toEntity());

        setCategoryItem(command.getCategoryId(), createdItem);

        return createdItem.getId();
    }

    /**
     * 단일 상품 조회
     * @return
     */
    @Override
    public ItemInfo.MainInfo getItem(Long id) {
        log.info("get item simple info by id={}", id);
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다." + id));

        return new ItemInfo.MainInfo(item);
    }

    @Override
    public <T> T itemDetail_V1(Long id) {
        log.info("get item detail by id={}", id);

        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다." + id));

        if (item instanceof Book) {
            log.info("item is book type");
            return (T) new ItemInfo.BookItemInfo((Book) item);
        } else if (item instanceof Album) {
            log.info("item is album type");
            return (T) new ItemInfo.AlbumItemInfo((Album) item);
        } else if (item instanceof Dvd) {
            log.info("item is dvd type");
            return (T) new ItemInfo.DvdItemInfo((Dvd) item);
        }

        return (T) new ItemInfo.DetailInfo<>(item);
    }

    @Override
    public ItemInfo.DetailInfo<Item> itemDetail_V2(Long id) {
        log.info("get item detail by id={}", id);
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다." + id));

        return new ItemInfo.DetailInfo<>(item);
    }

    /**
     * 상품명으로 상품 리스트 검색
     * @return
     */
    // Query Method 사용 버전 : case insensitive
    @Override
    public List<ItemInfo.MainInfo> searchItemsByName_V1(String name) {
        log.info("search item list by name={}", name);

        return itemRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(i -> !i.isDeleted())
                .map(ItemInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    // JPQL 사용 버전 : case insensitive
    @Override
    public List<ItemInfo.MainInfo> searchItemsByName_V2(String name) {
        log.info("search item list by name={}", name);
        return itemRepository.searchItemsByNameIgnoreCase(name)
                .stream()
                .map(ItemInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemInfo.MainInfo> items(ItemCommand.SearchCondition condition, Pageable pageable) {
        log.info("item paging list by search condition...");
        return itemRepository.findAll(ItemSearchCondition.of(condition), PageRequestUtils.of(pageable))
                .map(ItemInfo.MainInfo::new);
    }

    /**
     * 상품 전제 조회
     * @return
     */
    @Override
    public List<ItemInfo.MainInfo> itemList() {
        log.info("get item list...");

        return itemRepository.findAll()
                .stream()
                .filter(i -> !i.isDeleted())
                .map(ItemInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    /**
     * 상품 정보 업데이트
     */
    @Override
    @Transactional
    public void updateItemInfo(Long id, ItemCommand.UpdateInfoReq command) {
        log.info("updating item id={}", id);

        // 상품 엔티티 조회
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다." + id));

        // item 공통 부분 업데이트
        item.updateItem(command.getName(), command.getPrice(), command.getStockQuantity());

        // 상품 타입에 따른 업데이트
        if (item instanceof Book) {
            ((Book) item).updateBook(command.getAuthor(), command.getIsbn());
        } else if (item instanceof Album) {
            ((Album) item).updateAlbum(command.getArtist(), command.getEtc());
        } else if (item instanceof Dvd) {
            ((Dvd) item).updateDvd(command.getDirector(), command.getActor());
        }
    }

    /**
     * 상품 삭제
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("deleting item id={}", id);

        // 카테고리-상품 연관관계 정보 삭제
        categoryRepository.deleteCategoryItemsByItem_Id(id);

        // 싱품 엔티티 조회
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다." + id));

        // 상품 삭제
        item.delete();
    }

    // 상품 - 카테고리 설정 메소드
    private void setCategoryItem(Long categoryId, Item item) {
        log.info("setting category-item...");
        Category category = categoryRepository.findCategoryById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리 입니다. id = " + categoryId));

        CategoryItem.createCategoryItem(item, category);
    }
}
