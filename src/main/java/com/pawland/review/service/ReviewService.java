package com.pawland.review.service;

import com.pawland.order.domain.Order;
import com.pawland.order.exception.OrderException;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.review.domain.OrderReview;
import com.pawland.review.dto.request.CreateReviewRequest;
import com.pawland.review.dto.response.OrderReviewResponse;
import com.pawland.review.respository.OrderReviewJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final OrderReviewJpaRepository orderReviewJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderReviewResponse createReview(Long userId, Long orderId, CreateReviewRequest createReviewRequest) {
        User userById = getUserById(userId);
        Order orderById = getOrderById(orderId);

        if (!orderById.isBuyerCheck()) {
            throw new IllegalStateException("구매완료가 되지 않은 상품은 리뷰를 작성할수 없습니다.");
        }

        OrderReview orderReview = new OrderReview(orderById, userById, createReviewRequest.getContent(), createReviewRequest.getStar());

        orderReviewJpaRepository.save(orderReview);

        userById.addOrderReview(orderReview);
        orderById.addOrderReview(orderReview);

        return OrderReviewResponse.of(orderReview);
    }

    private User getUserById(Long buyerId) {
        return userRepository.findById(buyerId).orElseThrow(UserException.NotFoundUser::new);
    }

    private Order getOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(OrderException.NotFoundOrder::new);
    }
}
