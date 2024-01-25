package com.example.demo.Service.Impl;

import com.example.demo.Converter.CommentConverter;
import com.example.demo.Converter.CommentLikeConverter;
import com.example.demo.DTO.CommentDto;
import com.example.demo.DTO.CommentLikeDto;
import com.example.demo.DTO.CommentPageDto;
import com.example.demo.Entity.CommentEntity;
import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.CommentLikeRepository;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CommentService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentLikeConverter commentLikeConverter;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
            CommentConverter commentConverter,
            PostRepository postRepository,
            UserRepository userRepository,
            CommentLikeRepository commentLikeRepository,
            CommentLikeConverter commentLikeConverter) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.commentLikeConverter = commentLikeConverter;
    }

    private String GetProvider() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    @Override
    public CommentPageDto getCommentByPost(Long pid, Pageable pageable) {
        Optional<PostEntity> postEntity = postRepository.findByPid(pid);

        if (postEntity.isPresent()) {
            Page<CommentEntity> comments = commentRepository.findByPid(postEntity.get(), pageable);
            return commentConverter.toDto(comments);
        } else {
            return CommentPageDto.builder().build();
        }
    }

    @Override
    public CommentPageDto getCommentByUser(Long uid, Pageable pageable) {
        Optional<UserEntity> userEntity = userRepository.findByUid(uid);
        if (userEntity.isPresent()) {
            Page<CommentEntity> comments = commentRepository.findByUid(userEntity.get(), pageable);
            return commentConverter.toDto(comments);
        } else {
            return CommentPageDto.builder().build();
        }
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto) {
        Optional<UserEntity> auth = GetUserProv();
        Optional<UserEntity> user = userRepository.findByUid(commentDto.getUid());
        Optional<PostEntity> post = postRepository.findByPid(commentDto.getPid());

        if (auth.isPresent() && user.isPresent() && auth.get() == user.get() && post.isPresent()) {
            CommentEntity comment = commentRepository.save(
                    commentConverter.toEntity(commentDto, user.get(), post.get()));
            return commentConverter.toDto(comment);
        } else {
            return CommentDto.builder().build();
        }
    }

    @Override
    public Long updateComment(CommentDto commentDto) {
        Optional<CommentEntity> commentEntity = commentRepository.findByCid(commentDto.getCid());
        if (commentEntity.isPresent() && EqualUid(commentEntity.get())) {
            commentEntity.get().updateComment(commentDto.getContents());
            return commentDto.getCid();
        }
        return 0L;
    }

    @Override
    public Long deleteComment(Long cid) {
        Optional<CommentEntity> commentEntity = commentRepository.findByCid(cid);
        if (commentEntity.isPresent() && EqualUid(commentEntity.get())) {
            commentRepository.delete(commentEntity.get());
            return cid;
        }
        return 0L;
    }

    @Override
    public List<CommentLikeDto> getCommentLikeCid(Long cid, Boolean likes) {
        Optional<CommentEntity> commentEntity = commentRepository.findByCid(cid);
        if (commentEntity.isPresent()) {
            return commentLikeConverter.toDto(
                    commentLikeRepository.findByCidAndLikes(commentEntity.get(), likes));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<CommentLikeDto> getCommentLikeUid(Long uid, Boolean likes) {
        Optional<UserEntity> userEntity = userRepository.findByUid(uid);

        if (userEntity.isPresent()) {
            return commentLikeConverter.toDto(
                    commentLikeRepository.findByUidAndLikes(userEntity.get(), likes));
        } else {
            return Collections.emptyList();
        }
    }

    private Optional<UserEntity> GetUserProv() {
        return userRepository.findByProvider(GetProvider());
    }

    private Boolean EqualUid(CommentEntity commentEntity) {
        Optional<UserEntity> userEntity = GetUserProv();
        return userEntity.isPresent() && Objects.equals(commentEntity.getUid(), userEntity.get());
    }
}
