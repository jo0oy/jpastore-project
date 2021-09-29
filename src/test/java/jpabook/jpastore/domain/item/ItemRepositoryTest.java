package jpabook.jpastore.domain.item;

import jpabook.jpastore.application.ItemService;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("상품 추가 && 추가된 상품 조회")
    public void 추가된_상품_정보_조회() throws Exception {
        //given
        BookItemSaveRequestDto requestDto = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("김찬희")
                .price(15000)
                .stockQuantity(100)
                .isbn("1234")
                .build();

        itemService.saveBookItem(requestDto);
        //when
        List<Item> items = itemRepository.findAll();

        //then
        Item item = items.get(0);
        System.out.println(item);
        assertThat(item.getId()).isEqualTo(1L);
    }
}