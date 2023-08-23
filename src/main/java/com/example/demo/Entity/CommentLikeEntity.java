package com.example.demo.Entity;

import com.example.demo.Identifier.UidCid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "commentLike")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UidCid.class)
public class CommentLikeEntity extends BaseTimeEntity{

    @Id
    @ManyToOne
    @JoinColumn(name = "uid")
    private UserEntity uid;
    @Id
    @ManyToOne
    @JoinColumn(name = "cid")
    private CommentEntity cid;
    private boolean good;
    private boolean hate;

}
