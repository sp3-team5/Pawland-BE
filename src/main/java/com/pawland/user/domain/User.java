package com.pawland.user.domain;

import com.pawland.order.domain.Order;
import com.pawland.post.domain.Post;
import com.pawland.product.domain.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.pawland.user.domain.UserType.NORMAL;

@Entity(name = "users")
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;     // 이메일

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;   // 비밀번호

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;    // 전화번호

    private String introduce;   // 소개

    @Enumerated(EnumType.STRING)    // 회원 유형(구글, 카카오, 일반)
    private UserType type = NORMAL;

    private String profile_image;    // 이미지

    @OneToMany(mappedBy = "buyer")
    private List<Order> orderList = new ArrayList<>();    // 주문 내역

    @OneToMany(mappedBy = "seller")
    private List<Product> productList = new ArrayList<>();    // 등록한 상품

    @OneToMany(mappedBy = "author")
    private List<Post> postList = new ArrayList<>();    // 내가 쓴 글

    @Builder
    public User(String email, String password, String introduce, UserType type, String nickname, String profile_image) {
        this.email = email;
        this.password = password;
        this.introduce = introduce;
        this.type = type;
        this.nickname = nickname;
        this.profile_image = profile_image;
    }
}
