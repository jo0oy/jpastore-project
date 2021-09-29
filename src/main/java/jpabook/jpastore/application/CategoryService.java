package jpabook.jpastore.application;

import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.application.dto.category.CategoryResponseDto;
import jpabook.jpastore.dto.category.CategorySaveRequestDto;
import jpabook.jpastore.domain.category.queryRepo.CategoryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public Long createCategory(CategorySaveRequestDto requestDto) {

        Long parentId = requestDto.getParentId();
        Category parent = null;

        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다. id = " + parentId));
        }

        Category category = Category.createCategory(requestDto.getName(), parent);

        return categoryRepository.save(category).getId();
    }

    /**
     * 단일 카테고리 조회
     */
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다. id = " + id));
        return new CategoryResponseDto(category);
    }

    /**
     * 카테고리 상품 리스트 조회
     */
    // v1 : 카테고리에 포함된 상품 리스트만 조회
    public ItemListResponseDto getItemsByCategoryId_V1(Long id) {
        List<ItemResponseDto> items = categoryQueryRepository.findItemsByCategoryId(id)
                .stream()
                .map(CategoryItem::getItem)
                .map(ItemResponseDto::new)
                .collect(Collectors.toList());

        return new ItemListResponseDto(items);

    }

    // v2 : 카테고리 상품 리스트 조회 + 해당 카테고리 상세 정보 포함
    public List<CategoryResponseDto> getItemsByCategoryId_V2(Long id) {

        return categoryQueryRepository.findAllWithItems(id)
                .stream()
                .map(CategoryResponseDto::new)
                .collect(Collectors.toList());
    }


}
