package com.pawland.chat.service;

import com.pawland.chat.domain.ChatRoom;
import com.pawland.chat.dto.request.ChatRoomCreateRequest;
import com.pawland.chat.dto.response.ChatRoomInfoResponse;
import com.pawland.chat.repository.ChatRoomRepository;
import com.pawland.product.domain.Product;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.pawland.product.domain.Status.SELLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        productJpaRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방 생성 시")
    @Nested
    class createChatRoom {
        @DisplayName("요청 값 관련 테스트")
        @Nested
        class createChatRoom2 {
            @DisplayName("요청 값에 구매자 ID, 판매자 ID, 상품 ID가 모두 들어있으면 성공한다.")
            @Test
            void createChatRoom1() {
                // given
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .build();

                // when
                chatService.createChatRoom(buyer.getId(), request);
                List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
                ChatRoom result = chatRoomList.get(0);

                // then
                assertThat(chatRoomList.size()).isEqualTo(1L);
                assertThat(result.getBuyerId()).isEqualTo(buyer.getId());
                assertThat(result.getSellerId()).isEqualTo(seller.getId());
                assertThat(result.getProductId()).isEqualTo(product.getId());
            }

            @DisplayName("요청 값에 구매자, 판매자, 상품의 ID 중 하나라도 없으면 예외를 던진다.")
            @Test
            void createChatRoom2() {
                // given
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                ChatRoomCreateRequest requestWithoutSellerId = ChatRoomCreateRequest.builder()
                    .productId(product.getId())
                    .build();

                ChatRoomCreateRequest requestWithoutProductId = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutSellerId))
                    .isInstanceOf(RuntimeException.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithoutProductId))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @DisplayName("DB 정보 관련 테스트")
        @Nested
        class createChatRoom1 {
            @DisplayName("요청 값에 담긴 정보로 DB에서 판매자, 상품 정보 조회가 가능하면 성공한다.")
            @Test
            void createChatRoom1() {
                // given
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(product.getId())
                    .build();

                // when
                chatService.createChatRoom(buyer.getId(), request);
                List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
                ChatRoom result = chatRoomList.get(0);

                // then
                assertThat(chatRoomList.size()).isEqualTo(1L);
                assertThat(result.getBuyerId()).isEqualTo(buyer.getId());
                assertThat(result.getSellerId()).isEqualTo(seller.getId());
                assertThat(result.getProductId()).isEqualTo(product.getId());
            }

            @DisplayName("요청 값에 담긴 정보로 DB에서 판매자, 상품 정보 중 하나라도 조회하지 못하면 예외를 던진다.")
            @Test
            void createChatRoom2() {
                // given
                User buyer = createUser("구매자1", "midcon4@naver.com", "asd123123");
                User seller = createUser("판매자1", "midcon2@naver.com", "asd123123");
                userRepository.saveAll(List.of(buyer, seller));

                Product product = createProduct("나는짱물건", 10000, "장난감", "강아지", "새상품");
                productJpaRepository.save(product);

                Long InvalidSellerId = 0L;
                Long InvalidProductId = 0L;

                ChatRoomCreateRequest requestWithInvalidSellerInfo = ChatRoomCreateRequest.builder()
                    .sellerId(InvalidSellerId)
                    .productId(product.getId())
                    .build();

                ChatRoomCreateRequest requestWithInvalidProductInfo = ChatRoomCreateRequest.builder()
                    .sellerId(seller.getId())
                    .productId(InvalidProductId)
                    .build();

                List<ChatRoom> result = chatRoomRepository.findAll();

                // expected
                assertThat(result.size()).isEqualTo(0L);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidSellerInfo))
                    .isInstanceOf(UserException.NotFoundUser.class);
                assertThatThrownBy(() -> chatService.createChatRoom(buyer.getId(), requestWithInvalidProductInfo))
                    .isInstanceOf(ProductException.NotFoundProduct.class);
            }
        }
    }

    @DisplayName("내 채팅방 목록 조회 시")
    @Nested
    class getMyChatRoomList {
        @DisplayName("내가 구매자인 채팅방과 판매자인 채팅방을 모두 조회한다.")
        @Test
        void getMyChatRoomList1() {
            // given
            User myAccount = createUser("본인", "midcon1@naver.com", "asd123123");
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(myAccount, seller1, seller2, buyer1, buyer2));

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            Product product3 = createProduct("나는짱물건3", 3000, "장난감", "강아지", "새상품");
            Product product4 = createProduct("나는짱물건4", 4000, "장난감", "강아지", "새상품");
            product3.confirmPurchase(1L);
            productJpaRepository.saveAll(List.of(product1, product2, product3, product4));

            ChatRoom myChatRoom1 = createChatRoom(myAccount.getId(), seller1.getId(), product1.getId());
            ChatRoom myChatRoom2 = createChatRoom(myAccount.getId(), seller2.getId(), product2.getId());
            ChatRoom myChatRoom3 = createChatRoom(buyer1.getId(), myAccount.getId(), product3.getId());
            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller2.getId(), product4.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), product4.getId());
            chatRoomRepository.saveAll(List.of(myChatRoom1, myChatRoom2, myChatRoom3, notMyChatRoom1, notMyChatRoom2));

            // when
            List<ChatRoomInfoResponse> result = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting("opponentUser")
                .extracting("nickname")
                .containsExactlyInAnyOrder("판매자1", "판매자2", "구매자1");
            assertThat(result).extracting("productInfo")
                .extracting("price", "productName", "saleState", "purchaser")
                .containsExactlyInAnyOrder(
                    tuple(1000, "나는짱물건1", SELLING, null),
                    tuple(2000, "나는짱물건2", SELLING, null),
                    tuple(3000, "나는짱물건3", SELLING, 1L)
                );
        }

        @DisplayName("내가 참여하고 있는 채팅방이 없을 시 빈 리스트를 반환한다.")
        @Test
        void getMyChatRoomList2() {
            User myAccount = createUser("본인", "midcon1@naver.com", "asd123123");
            User seller1 = createUser("판매자1", "midcon2@naver.com", "asd123123");
            User seller2 = createUser("판매자2", "midcon3@naver.com", "asd123123");
            User buyer1 = createUser("구매자1", "midcon4@naver.com", "asd123123");
            User buyer2 = createUser("구매자2", "midcon5@naver.com", "asd123123");
            userRepository.saveAll(List.of(myAccount, seller1, seller2, buyer1, buyer2));

            Product product1 = createProduct("나는짱물건1", 1000, "장난감", "강아지", "새상품");
            Product product2 = createProduct("나는짱물건2", 2000, "장난감", "강아지", "새상품");
            productJpaRepository.saveAll(List.of(product1, product2));

            ChatRoom notMyChatRoom1 = createChatRoom(buyer1.getId(), seller1.getId(), product1.getId());
            ChatRoom notMyChatRoom2 = createChatRoom(buyer2.getId(), seller2.getId(), product2.getId());
            chatRoomRepository.saveAll(List.of(notMyChatRoom1, notMyChatRoom2));

            // when
            List<ChatRoomInfoResponse> result = chatRoomRepository.getMyChatRoomList(myAccount.getId());

            // then
            assertThat(result).hasSize(0);
        }
    }

    private static User createUser(String nickname, String email, String password) {
        return User.builder()
            .nickname(nickname)
            .email(email)
            .password(password)
            .build();
    }

    private Product createProduct(String name, int price, String category, String species, String condition) {
        return Product.builder()
            .name(name)
            .price(price)
            .category(category)
            .species(species)
            .condition(condition)
            .build();
    }

    private static ChatRoom createChatRoom(Long buyerId, Long sellerId, Long productId) {
        ChatRoom chatRoom = ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .productId(productId)
            .build();
        return chatRoom;
    }
}
