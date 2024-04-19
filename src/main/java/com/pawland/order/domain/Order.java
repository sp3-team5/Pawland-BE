package com.pawland.order.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.product.domain.Product;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    @ManyToOne
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order(User seller, User buyer, Product product) {
        this.seller = seller;
        this.buyer = buyer;
        this.product = product;
        this.status = OrderStatus.PROCEEDING;
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}
