package jpabook.jpastore.web.validator;


import jpabook.jpastore.web.dto.item.ItemDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class ItemUpdateRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ItemDto.UpdateInfoReq.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ItemDto.UpdateInfoReq request = (ItemDto.UpdateInfoReq) target;

        if (Objects.nonNull(request.getPrice()) && request.getPrice() < 1000) {
            errors.rejectValue("price", "Range");
        }

        if (Objects.nonNull(request.getStockQuantity())
                && (request.getStockQuantity() < 100 || request.getStockQuantity() > 10000)) {
            errors.rejectValue("stockQuantity", "Range");
        }
    }
}
