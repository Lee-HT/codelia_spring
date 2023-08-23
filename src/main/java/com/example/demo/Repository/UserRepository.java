package com.example.demo.Repository;

import com.example.demo.Entity.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByUsername(@Nonnull String name);

}
