package com.example.demo.Repository;

import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.PostLikeEntity;
import com.example.demo.Entity.UserEntity;
import io.jsonwebtoken.lang.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PostLikeRepositoryTest {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private List<PostLikeEntity> postLikes = new ArrayList<>();
    private final List<PostEntity> posts = new ArrayList<>();
    private final List<UserEntity> users = new ArrayList<>();

    @Autowired
    public PostLikeRepositoryTest(PostLikeRepository postLikeRepository,
            PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setPostLikes() {
        for (int i = 1; i < 4; i++) {
            users.add(UserEntity.builder().username("user" + i)
                    .email("email" + i + "@gmail.com").provider("google_" + i).build());
        }
        for (int i = 1; i < 6; i++) {
            posts.add(PostEntity.builder().uid(users.get(i / 2)).title("title" + i)
                    .contents("contents" + i)
                    .category("category" + i).build());
        }
        for (int i = 0; i < 5; i++) {
            postLikes.add(
                    PostLikeEntity.builder().uid(users.get(i / 3)).pid(posts.get(i))
                            .likes(i % 2 == 0).build());
        }

        userRepository.saveAll(users);
        postRepository.saveAll(posts);
        this.postLikes = postLikeRepository.saveAll(postLikes);
    }

    @Test
    void findAll() {
        List<PostLikeEntity> postLikes = postLikeRepository.findAll();

        Assertions.assertThat(postLikes).usingRecursiveComparison().isEqualTo(this.postLikes);
    }

    @Test
    void findByUid() {
        List<PostLikeEntity> postLikes = postLikeRepository.findByUid(users.get(0));

        Assertions.assertThat(postLikes).usingRecursiveComparison().isEqualTo(this.postLikes.subList(0, 3));
    }

    @Test
    void findByPidAndUid() {
        Optional<PostLikeEntity> result = postLikeRepository.findByPidAndUid(posts.get(0), users.get(0));

        Assert.isTrue(result.isPresent());
        Assertions.assertThat(result.get()).usingRecursiveComparison().isEqualTo(postLikes.get(0));
    }

    @Test
    void saveAll() {
        List<PostLikeEntity> postLikes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            postLikes.add(
                    PostLikeEntity.builder().uid(users.get(2)).pid(posts.get(i)).likes(i % 2 == 0)
                            .build());
        }
        List<PostLikeEntity> result = postLikeRepository.saveAll(postLikes);

        Assertions.assertThat(result).usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
                .isEqualTo(postLikes);
    }

    @Test
    void countByPid() {
        Long countLikes = postLikeRepository.countByPidAndLikes(posts.get(0), true);

        Assertions.assertThat(countLikes).isEqualTo(1L);
    }
}
