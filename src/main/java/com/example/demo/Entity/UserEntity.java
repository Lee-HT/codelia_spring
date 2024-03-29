package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Builder
@Table(name = "user")
@AllArgsConstructor
// new 로 생성 불가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @Column(nullable = false, length = 31)
    private String username;
    @Column(nullable = false, length = 127, unique = true)
    private String email;
    @Column(nullable = false, length = 63, unique = true)
    private String provider;
    private String roles;
    private String profilePic;

    public void updateUser(String username, String email, String profilePic) {
        this.username = username;
        this.email = email;
        this.profilePic = profilePic;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfileImg(String img) {
        this.profilePic = img;
    }
}
