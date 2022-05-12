package jpabook.jpastore.application.category;

import jpabook.jpastore.application.dto.category.CategoryListResponseDto;
import jpabook.jpastore.application.dto.category.CategoryParentChildDto;
import jpabook.jpastore.application.dto.category.CategoryResponseDto;
import jpabook.jpastore.application.dto.category.CategorySingleResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.dto.category.CategorySaveRequestDto;
import jpabook.jpastore.dto.category.CategoryUpdateReqDto;
import jpabook.jpastore.exception.CannotDeleteException;
import jpabook.jpastore.exception.CategoryNotFoundException;
import jpabook.jpastore.exception.DuplicateNameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성
     */
    @Override
    @Transactional
    public Long create(CategorySaveRequestDto requestDto) {

        log.info("creating category...");

        duplicateNameCheck(requestDto.getName());
        Long parentId = requestDto.getParentId();
        Category parent = null;

        if (Objects.nonNull(parentId)) {
            log.info("finding parent category...parentId = {}", parentId);
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> {
                        log.error("존재하지 않는 카테고리입니다. id = " + parentId);
                        throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + parentId);
                    });
        }

        Category category = Category.createCategory(requestDto.getName(), parent);

        return categoryRepository.save(category).getId();
    }

    /**
     * 단일 카테고리 조회
     */

    // 1. 아이디로 조회
    // v1. dto 변환 과정에서 불필요한 parent category 까지 in 쿼리에 포함.
    @Override
    public CategoryResponseDto getCategory(Long id) {
        log.info("finding category by id = {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 카테고리입니다. id={}", id);
                    throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + id);
                });

        return new CategoryResponseDto(category);
    }

    // v2. 컬렉션 매핑관계에 접근하는 순서 변동한 CategorySingleResponseDto 로 변환(v1 문제 해결)
    // -> parent 를 가장 마지막에 접근
    @Override
    public CategorySingleResponseDto getCategoryV2(Long id) {
        log.info("finding category by id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 카테고리입니다. id={}", id);
                    throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + id);
                });

        return new CategorySingleResponseDto(category);
    }

    // v3. parent fetch join 으로 가져올 경우 v1과 동일한 문제 발생.
    @Override
    public CategorySingleResponseDto getCategoryV3(Long id) {
        log.info("finding category by id = {}", id);

        Category category = categoryRepository.findByIdFetchJoin(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 카테고리입니다. id={}", id);
                    throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + id);
                });

        return new CategorySingleResponseDto(category);
    }


    /**
     * 카테고리 전체 리스트 조회 (parent-child 계층 구조)
     */

    // parent id is null 인 카테고리 조회 -> dto 로 넘기기
    // dto 에서 getChild()를 통해 in 쿼리로 child 가져옴. (카테고리 깊이 + 1번 만큼 in 쿼리 발생)
    @Override
    public CategoryListResponseDto<CategoryParentChildDto> categoryList() {
        log.info("find all categories tree list...");

        return new CategoryListResponseDto<>(categoryRepository.totalCount(),
                categoryRepository.findAllRootParents().stream()
                .map(CategoryParentChildDto::new)
                .collect(Collectors.toList()));
    }

    /**
     * 카테고리-상품 리스트 조회
     */
    @Override
    public List<CategoryResponseDto> list() {
        return categoryRepository.findAllFetchJoin().stream()
                .map(CategoryResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public ItemListResponseDto<ItemResponseDto> itemListByCategoryId(Long categoryId) {
        log.info("finding simple items info by category id={}", categoryId);

        return new ItemListResponseDto<>(
                categoryRepository.findItemsByCategoryId(categoryId)
                .stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList())
        );
    }

    // v2 : 카테고리 상품 리스트 조회 + 해당 카테고리 상세 정보 포함
    @Override
    public CategoryResponseDto categoryWithItems(Long categoryId) {
        log.info("finding category and items by category id={}", categoryId);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("존재하지 않는 카테고리입니다. id={}", categoryId);
            throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + categoryId);
        });

        return new CategoryResponseDto(category);
    }

    /**
     * 카테고리 수정
     */
    @Override
    @Transactional
    public void update(Long categoryId, CategoryUpdateReqDto requestDto) {
        log.info("updating category id={}", categoryId);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("존재하지 않는 카테고리입니다. id={}", categoryId);
            throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + categoryId);
        });

        Category parent = null;
        if (Objects.nonNull(requestDto.getParentId())) {
            parent = categoryRepository.findByIdFetchJoin(requestDto.getParentId()).orElseThrow(() -> {
                log.error("존재하지 않는 카테고리입니다. id={}", requestDto.getParentId());
                throw new CategoryNotFoundException("존재하지 않는 카테고리입니다. id = " + requestDto.getParentId());
            });
        }

        category.update(parent, requestDto.getName());
    }

    /**
     * 카테고리 삭제 -> 자식 카테고리가 없는 경우만 삭제 가능
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("deleting category id={}", id);

        validateDelete(id);

        categoryRepository.deleteById(id);
    }

    // 삭제 가능 카테고리 검증 메서드
    private void validateDelete(Long id) {
        log.info("validating ok to delete category id={}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("삭제할 수 없는 카테고리 입니다. 존재하지 않는 카테고리입니다. id={}", id);
            throw new CannotDeleteException("삭제할 수 없는 카테고리 입니다. 존재하지 않는 카테고리입니다. id = " + id);
        });

        Optional.ofNullable(categoryRepository.findChildIdsByParentId(id))
                .ifPresent(list -> {
                    log.error("삭제할 수 없는 카테고리 입니다. 자식 카테고리가 존재합니다. id={}", id);
                    throw new CannotDeleteException("삭제할 수 없는 카테고리 입니다. 자식 카테고리가 존재합니다. id = " + id);
                });
    }

    // 카테고리 중복 이름 체크 메서드
    private void duplicateNameCheck(String name) {

        log.info("duplicate category name checking...");
        categoryRepository.findByName(name).ifPresent(
                category -> {
                    log.error("이미 존재하는 카테고리 이름입니다. name={}", name);
                    throw new DuplicateNameException("이미 존재하는 카테고리 이름입니다. name=" + name);
                }
        );
    }
}
