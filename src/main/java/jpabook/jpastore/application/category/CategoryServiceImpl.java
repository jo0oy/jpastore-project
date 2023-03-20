package jpabook.jpastore.application.category;

import jpabook.jpastore.common.exception.CannotDeleteException;
import jpabook.jpastore.common.exception.DuplicateNameException;
import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
    public Long registerCategory(CategoryCommand.RegisterReq command) {

        log.info("register category...");

        Long parentId = command.getParentId();
        Category parent = null;

        // 1. 부모 카테고리 null 아닌 경우, 엔티티 조회.
        if (Objects.nonNull(parentId)) {
            log.info("finding parent category...parentId = {}", parentId);
            parent = categoryRepository.findCategoryById(parentId)
                    .orElseThrow(() -> entityNotFoundException(parentId));
        }

        // 2. 카테고리 이름 검증
        duplicateNameCheck(command.getName(), parent);

        // 3. 카테고리 엔티티 생성.
        Category category = Category.createCategory(command.getName(), parent);

        return categoryRepository.save(category).getId();
    }

    /**
     * 단일 카테고리 조회
     * @return
     */

    // 1. 아이디로 조회
    // v1. 컬렉션 매핑관계에 접근하는 순서 변동한 CategoryInfo.MainInfo 로 변환 (부모의 자식까지 in 쿼리로 조회하는 문제 해결)
    // -> parent 를 가장 마지막에 접근
    @Override
    public CategoryInfo.MainInfo getCategory(Long id) {
        log.info("finding category by id={}", id);

        Category category = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> entityNotFoundException(id));

        return new CategoryInfo.MainInfo(category);
    }

    // v2. parent fetch join 으로 가져올 경우 v1과 동일한 문제 발생.
    @Override
    public CategoryInfo.MainInfo getCategoryV2(Long id) {
        log.info("finding category by id = {}", id);

        Category category = categoryRepository.findCategoryByIdWithParent(id)
                .orElseThrow(() -> entityNotFoundException(id));

        return new CategoryInfo.MainInfo(category);
    }

    // 카테고리 상세 정보 조회 (부모, 자식 카테고리 정보, 상품리스트 포함)
    // 카테고리 상품 리스트 조회 + 해당 카테고리 상세 정보 포함
    @Override
    public CategoryInfo.DetailWithItemsInfo getCategoryWithItems(Long id) {
        log.info("finding category by id = {}", id);

        Category category = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> entityNotFoundException(id));

        return new CategoryInfo.DetailWithItemsInfo(category);
    }


    /**
     * 카테고리 전체 리스트 조회 (parent-child 계층 구조)
     * @return
     */
    // parent id is null 인 카테고리 조회 -> dto 로 넘기기
    // dto 에서 getChild()를 통해 in 쿼리로 child 가져옴. (카테고리 깊이 + 1번 만큼 in 쿼리 발생)
    @Override
    public List<CategoryInfo.ParentChildInfo> categoryHierarchicalList() {
        log.info("find all categories tree list...");

        return categoryRepository.findAllRootParents().stream()
                .map(CategoryInfo.ParentChildInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryInfo.MainInfo> categoryList() {
        return categoryRepository.findAll().stream()
                .filter(i -> !i.isDeleted())
                .map(CategoryInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    /**
     * 전체 카테고리-상품 리스트 조회
     * @return
     */
    @Override
    public List<CategoryInfo.DetailWithItemsInfo> categoryDetailList() {
        return categoryRepository.findAllWithParents().stream()
                .map(CategoryInfo.DetailWithItemsInfo::new)
                .collect(Collectors.toList());
    }

    // categoryId에 해당하는 카테고리의 상품 리스트 정보 조회
    @Override
    public CategoryInfo.CategoryItemListInfo categoryItemListByCategoryId(Long categoryId) {
        log.info("finding simple items info by category id={}", categoryId);

        var category = categoryRepository.findCategoryById(categoryId)
                .orElseThrow(() -> entityNotFoundException(categoryId));

        return new CategoryInfo.CategoryItemListInfo(category);
    }

    /**
     * 카테고리 수정
     */
    @Override
    @Transactional
    public void update(Long categoryId, CategoryCommand.UpdateInfoReq command) {
        log.info("updating category id={}", categoryId);

        // 카테고리 정보 변경할 내용
        var updateName = command.getName();
        var updateParent = command.getParentId();
        Category parent = null;

        // 변경할 카테고리 조회
        Category category = categoryRepository.findCategoryById(categoryId)
                        .orElseThrow(() -> entityNotFoundException(categoryId));


        // updateParent 가 null 인 경우 -> parent 변경 안한다. -> 이전 내용 유지
        // updateParent 가 0인 경우 -> root 카테고리로 변경한다.
        if (Objects.isNull(updateParent)) {
            parent = category.getParent();
        } else if (updateParent > 0) {
            parent = categoryRepository.findCategoryById(updateParent)
                    .orElseThrow(() -> entityNotFoundException(updateParent));
        }

        // updateName 이 null 인 경우 -> update 안함 -> 이전 이름 그대로 사용
        if (Objects.isNull(updateName)) {
            updateName = category.getName();
        }

        // 수정할 내용이 있는 경우에만 이름 중복 체크
        if (!category.getName().equals(updateName) || !category.getParent().equals(parent)) {
            log.info("수정할 내용 존재....");
            // 수정할 카테고리명 중복 체크
            duplicateNameCheck(command.getName(), parent);
        }


        log.info("update to name={}, parentId={}", updateName, (parent == null) ? null : parent.getId());
        category.update(parent, updateName);
    }

    /**
     * 카테고리 삭제 -> 자식 카테고리가 없는 경우만 삭제 가능
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("deleting category id={}", id);

        // 연관된 CategoryItems 벌크 삭제
        categoryRepository.deleteCategoryItemsByCategory_Id(id);

        Category category = categoryRepository.findCategoryById(id)
                .orElseThrow(() -> entityNotFoundException(id));

        validateDelete(category);

        category.delete();
    }

    // 삭제 가능 카테고리 검증 메서드
    private void validateDelete(Category category) {
        log.info("validating is ok to delete category id={}", category.getId());

        if (category.getChild().size() > 0) {
            log.error("삭제할 수 없는 카테고리 입니다. id={}", category.getId());
            throw new CannotDeleteException("삭제할 수 없는 카테고리 입니다. 자식 카테고리가 존재합니다. id = " + category.getId());
        }
    }

    // 카테고리 중복 이름 체크 메서드
    private void duplicateNameCheck(String name, Category parent) {

        log.info("duplicate category name checking...");

        /**
         * 1. parent is NULL 인 경우 : 중복 Name 하나라도 있는 경우 예외 발생.
         * 2. parent name 와 name 동일한 경우 : 예외 발생.
         * 3. 같은 depth(형제) 카테고리 리스트 확인 : 중복 Name 하나라도 있는 경우 예외 발생.
         */

        if (Objects.isNull(parent)) {
            if (!categoryRepository.findAllByName(name).isEmpty()) {
                throw duplicateNameException(name);
            }
        } else if(parent.getName().equals(name)){
            throw duplicateNameException(name);
        } else {
            var list = categoryRepository.findCategoryIdsInChildIdsAndEqName(parent.getId(), name);

            log.info("duplicatedChildList size={}", list.size());

            if (!list.isEmpty()) {
                throw duplicateNameException(name);
            }
        }

    }

    private DuplicateNameException duplicateNameException(String name) {
        log.error("중복된 카테고리 이름입니다. name={}", name);
        return new DuplicateNameException("중복된 카테고리 이름입니다. name=" + name);
    }

    private EntityNotFoundException entityNotFoundException(Long id) {
        log.error("존재하지 않는 카테고리입니다. id={}", id);
        return new EntityNotFoundException("존재하지 않는 카테고리입니다. id = " + id);
    }
}
