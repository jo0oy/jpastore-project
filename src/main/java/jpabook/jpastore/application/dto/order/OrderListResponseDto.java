package jpabook.jpastore.application.dto.order;

import jpabook.jpastore.domain.order.Order;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderListResponseDto<T> {

    private int totalCount;
    private List<T> orders = new ArrayList<>();

    public OrderListResponseDto(List<T> list) {
        this.totalCount = list.size();
        for (T order : list) {
            if(order instanceof Order) {
                orders.add((T) new OrderResponseDto((Order) order));
            }else{
                orders.add(order);
            }
        }
    }
}
