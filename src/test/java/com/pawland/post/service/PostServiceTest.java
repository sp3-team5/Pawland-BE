package com.pawland.post.service;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.Region;
import com.pawland.post.dto.request.PostWriteRequest;
import com.pawland.post.repository.PostRepository;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.pawland.global.domain.DefaultImage.DEFAULT_POST_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        postRepository.deleteAllInBatch();
    }

    @DisplayName("게시글 작성 시")
    @Nested
    class uploadPost {
        @DisplayName("필수 값 입력 시")
        @Nested
        class uploadPostWithEssential {
            @DisplayName("입력 값대로 저장된다.")
            @Test
            void uploadPost1() {
                // given
                User user = User.builder()
                    .nickname("나는짱")
                    .email("midcon@naver.com")
                    .password("asd123123")
                    .build();
                userRepository.save(user);

                PostWriteRequest request = PostWriteRequest.builder()
                    .title("제목")
                    .content("내용")
                    .thumbnail("이미지.png")
                    .region("충남")
                    .build();

                // when
                postService.uploadPost(user.getId(), request);
                List<Post> postList = postRepository.findAll();
                Post result = postList.get(0);

                // then
                assertThat(postList.size()).isEqualTo(1);
                assertThat(result.getTitle()).isEqualTo("제목");
                assertThat(result.getContent()).isEqualTo("내용");
                assertThat(result.getThumbnail()).isEqualTo("이미지.png");
                assertThat(result.getRegion()).isEqualTo(Region.CHUNGNAM);
            }

            @DisplayName("썸네일과 지역을 입력하지 않으면 기본 값으로 저장된다.")
            @Test
            void uploadPost2() {
                // given
                User user = User.builder()
                    .nickname("나는짱")
                    .email("midcon@naver.com")
                    .password("asd123123")
                    .build();
                userRepository.save(user);

                PostWriteRequest request = PostWriteRequest.builder()
                    .title("제목")
                    .content("내용")
                    .build();

                // when
                postService.uploadPost(user.getId(), request);
                List<Post> postList = postRepository.findAll();
                Post result = postList.get(0);

                // then
                assertThat(postList.size()).isEqualTo(1);
                assertThat(result.getTitle()).isEqualTo("제목");
                assertThat(result.getContent()).isEqualTo("내용");
                assertThat(result.getThumbnail()).isEqualTo(DEFAULT_POST_IMAGE.value());
                assertThat(result.getRegion()).isEqualTo(Region.SEOUL);
            }

            @DisplayName("설정 지역 외의 지역 입력 시 예외가 발생한다.")
            @Test
            void uploadPost3() {
                // given
                User user = User.builder()
                    .nickname("나는짱")
                    .email("midcon@naver.com")
                    .password("asd123123")
                    .build();
                userRepository.save(user);

                PostWriteRequest request = PostWriteRequest.builder()
                    .title("제목")
                    .content("내용")
                    .region("나는짱")
                    .build();

                // expected
                assertThatThrownBy(() -> postService.uploadPost(user.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지역 값을 확인해주세요.");
            }
        }

        @DisplayName("필수 값 누락 시 예외가 발생한다.")
        @Test
        void uploadPost1() {
            // given
            User user = User.builder()
                .nickname("나는짱")
                .email("midcon@naver.com")
                .password("asd123123")
                .build();
            userRepository.save(user);

            PostWriteRequest request = PostWriteRequest.builder()
                .thumbnail("이미지.png")
                .region("충남")
                .build();

            // when
            assertThatThrownBy(() -> postService.uploadPost(user.getId(), request))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }
}