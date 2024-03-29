package com.example.demo.Entity;

import com.example.demo.Identifier.UidPid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Builder
@Table(name = "postLike")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(UidPid.class)
@ToString(callSuper = true)
public class PostLikeEntity extends BaseTimeEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "uid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity uid;
    @Id
    @ManyToOne
    @JoinColumn(name = "pid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PostEntity pid;
    private Boolean likes;
    public void updateLikes(Boolean likes) {
        this.likes = likes;
    }

}
