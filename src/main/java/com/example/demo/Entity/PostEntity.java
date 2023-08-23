package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private UserEntity username;
    @Column(nullable = false, length = 128)
    private String title;
    @Column(nullable = false, length = 1024)
    private String contents;

}
