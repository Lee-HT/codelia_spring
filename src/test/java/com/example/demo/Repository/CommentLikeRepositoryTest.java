package com.example.demo.Repository;

import com.example.demo.Entity.CommentEntity;
import com.example.demo.Entity.CommentLikeEntity;
import com.example.demo.Entity.UserEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CommentLikeRepositoryTest {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private List<CommentLikeEntity> commentLikes = new ArrayList<>();
    private List<CommentEntity> comments = new ArrayList<>();
    private List<UserEntity> users = new ArrayList<>();

    @Autowired
    public CommentLikeRepositoryTest(CommentLikeRepository commentLikeRepository,
            CommentRepository commentRepository, UserRepository userRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // commentLikes의 ID가 null이 아니라 Detached 상태로 판단 -> merge 수행
    @BeforeEach
    public void setCommentLikes() {
        for (int i = 1; i < 4; i++) {
            users.add(UserEntity.builder().username("user" + i).email("email" + i + "@gmail.com")
                    .provider("google_" + i).build());
        }
        for (int i = 1; i < 6; i++) {
            comments.add(CommentEntity.builder().uid(users.get(i / 2)).contents("contents" + i)
                    .build());
        }
        for (int i = 0; i < 5; i++) {
            commentLikes.add(CommentLikeEntity.builder().uid(users.get(i / 3)).cid(comments.get(i))
                    .likes(i % 2 == 0).build());
        }

        userRepository.saveAll(users);
        commentRepository.saveAll(comments);
        this.commentLikes = commentLikeRepository.saveAll(commentLikes);
    }

    @Test
    @DisplayName("전체 SELECT")
    public void findAll() {
        System.out.println("======== findAll ========");
        List<CommentLikeEntity> commentLikes = commentLikeRepository.findAll();

        System.out.println(commentLikes);
        System.out.println(this.commentLikes);

        Assertions.assertThat(commentLikes).usingRecursiveComparison()
                .isEqualTo(this.commentLikes);
    }

    @Test
    @DisplayName("UID 기준 SELECT")
    public void findByUid() {
        System.out.println("======== findByUid ========");
        List<CommentLikeEntity> result = commentLikeRepository.findByUidAndLikes(users.get(0),
                true);
        List<CommentLikeEntity> commentLikes = new ArrayList<>();
        List<Integer> idxs = Arrays.asList(0, 2);
        for (int i : idxs) {
            commentLikes.add(this.commentLikes.get(i));
        }

        System.out.println(result);

        Assertions.assertThat(result).usingRecursiveComparison()
                .isEqualTo(commentLikes);
    }

    @Test
    @DisplayName("INSERT")
    public void saveAll() {
        System.out.println("======== saveAll ========");
        List<CommentLikeEntity> commentLikes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            commentLikes.add(CommentLikeEntity.builder().uid(users.get(2)).cid(comments.get(i))
                    .likes(i % 2 == 0).build());
        }
        List<CommentLikeEntity> result = commentLikeRepository.saveAll(commentLikes);

        System.out.println(result);

        Assertions.assertThat(result).usingRecursiveComparison()
                .ignoringFields("createdAt","updatedAt").isEqualTo(commentLikes);
    }

    @Test
    @DisplayName("CID COUNT SELECT")
    public void countByCidAndLikes() {
        System.out.println("======== countByCid ========");
        boolean likes = true;
        int countLikes = commentLikeRepository.countByCidAndLikes(comments.get(0), likes);

        System.out.println(countLikes);

        Assertions.assertThat(countLikes).usingRecursiveComparison().isEqualTo(1);
    }

}