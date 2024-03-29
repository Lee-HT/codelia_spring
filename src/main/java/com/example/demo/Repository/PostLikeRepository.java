package com.example.demo.Repository;

import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.PostLikeEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Identifier.UidPid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, UidPid> {
    Optional<PostLikeEntity> findByPidAndUid(PostEntity pid,UserEntity uid);
    void deleteByPidAndUid(PostEntity pid, UserEntity uid);
    List<PostLikeEntity> findByUid(UserEntity uid);
    Long countByPidAndLikes(PostEntity pid,Boolean likes);
}
