package jpabook.jpastore.domain.item;

import jpabook.jpastore.application.item.ItemCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ItemSearchCondition {
    private String name;
    private Integer minPrice;
    private Integer maxPrice;

    public static ItemSearchCondition of(ItemCommand.SearchCondition condition) {
        return ItemSearchCondition.builder()
                .name((StringUtils.hasText(condition.getName())) ? condition.getName() : null)
                .minPrice((condition.getMinPrice() != null) ? condition.getMinPrice() : null)
                .maxPrice((condition.getMaxPrice() != null) ? condition.getMaxPrice() : null)
                .build();
    }
}
