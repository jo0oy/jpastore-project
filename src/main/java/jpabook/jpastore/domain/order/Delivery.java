package jpabook.jpastore.domain.order;

import jpabook.jpastore.domain.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Builder
    public Delivery(Order order, Address address, DeliveryStatus status) {
        this.order = order;
        this.address = address;
        this.status = status;
    }

    //==연관관계 메서드==//
    public void setOrder(Order order) {
        this.order = order;
    }

    //==비즈니스 로직==//
    //==배달 상태 변경 메서드==//
    public void changeStatus() {
        this.status = status.getNext();
    }

    public void changeStatus(DeliveryStatus status) {
        this.status = status;
    }

    //==취소==//
    public void cancel() {
        this.status = DeliveryStatus.NONE;
    }
}
