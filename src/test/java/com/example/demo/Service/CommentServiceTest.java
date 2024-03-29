package com.example.demo.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.Converter.CommentConverter;
import com.example.demo.Converter.CommentLikeConverter;
import com.example.demo.DTO.CommentDto;
import com.example.demo.DTO.CommentLikeDto;
import com.example.demo.DTO.CommentPageDto;
import com.example.demo.Entity.CommentEntity;
import com.example.demo.Entity.CommentLikeEntity;
import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.CommentLikeRepository;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.Impl.CommentServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private CommentConverter commentConverter;
    @Mock
    private CommentLikeConverter commentLikeConverter;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    private final Pageable pageable = PageRequest.of(0, 3, Direction.DESC, "cid");
    private final UserEntity userEntity = UserEntity.builder().uid(1L).build();
    private final CommentPageDto commentPageDto = CommentPageDto.builder().build();
    private final CommentLikeDto commentLikeDto = CommentLikeDto.builder().build();

    @Test
    void getPostCommentPage() {
        when(postRepository.findByPid(anyLong())).thenReturn(
                Optional.of(PostEntity.builder().build()));
        when(commentRepository.findByPid(any(PostEntity.class), any(Pageable.class))).thenReturn(
                new PageImpl<>(new ArrayList<>()));
        when(commentConverter.toDto(ArgumentMatchers.<Page<CommentEntity>>any())).thenReturn(
                commentPageDto);

        CommentPageDto result = commentService.getCommentByPost(1L, pageable);
        Assertions.assertThat(result).isEqualTo(commentPageDto);
    }

    @Test
    void getUserCommentPage() {
        when(userRepository.findByUid(anyLong())).thenReturn(
                Optional.of(userEntity));
        when(commentRepository.findByUid(any(UserEntity.class), any(Pageable.class))).thenReturn(
                new PageImpl<>(new ArrayList<>()));
        when(commentConverter.toDto(ArgumentMatchers.<Page<CommentEntity>>any())).thenReturn(commentPageDto);

        CommentPageDto result = commentService.getCommentByUser(1L, pageable);
        Assertions.assertThat(result).isEqualTo(commentPageDto);
    }

    @Test
    void getCommentLikeCid() {
        when(commentRepository.findByCid(anyLong())).thenReturn(
                Optional.of(CommentEntity.builder().build()));
        when(commentLikeRepository.findByCid(any(CommentEntity.class))).thenReturn(new ArrayList<>());
        when(commentLikeConverter.toDto(anyList())).thenReturn(Collections.singletonList(commentLikeDto));

        List<CommentLikeDto> result = commentService.getCommentLikeCid(1L);
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(Collections.singletonList(commentLikeDto));
    }

    @Test
    void getCommentLikeUid() {
        when(userRepository.findByUid(anyLong())).thenReturn(
                Optional.of(userEntity));
        when(commentLikeRepository.findByUid(any(UserEntity.class))).thenReturn(new ArrayList<>());
        when(commentLikeConverter.toDto(anyList())).thenReturn(Collections.singletonList(commentLikeDto));

        List<CommentLikeDto> result = commentService.getCommentLikeUid(1L);
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(Collections.singletonList(commentLikeDto));
    }

    @Test
    void getCommentLikeByUidPid() {
        setUserProv();
        when(commentRepository.findByCid(anyLong())).thenReturn(Optional.of(CommentEntity.builder().build()));
        when(commentLikeRepository.findByCidAndUid(any(CommentEntity.class), any(UserEntity.class))).thenReturn(
                Optional.of(CommentLikeEntity.builder().build()));
        when(commentLikeConverter.toDto(any(CommentLikeEntity.class))).thenReturn(commentLikeDto);

        CommentLikeDto result = commentService.getCommentLikeByUidPid(1L,1L);
        Assertions.assertThat(result).isEqualTo(commentLikeDto);
    }

    @Test
    void getCountCommentLike() {
        when(commentRepository.findByCid(anyLong())).thenReturn(Optional.of(CommentEntity.builder().build()));
        when(commentLikeRepository.countByCid(any(CommentEntity.class))).thenReturn(2L);

        Long result = commentService.getCountCommentLike(1L);
        Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void saveComment() {
        setUserProv();
        when(postRepository.findByPid(any(Long.class))).thenReturn(
                Optional.of(PostEntity.builder().uid(userEntity).build()));
        when(commentConverter.toEntity(any(CommentDto.class), any(UserEntity.class),
                any(PostEntity.class))).thenReturn(CommentEntity.builder().build());
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(
                CommentEntity.builder().cid(1L).build());

        Long result = commentService.saveComment(CommentDto.builder().pid(1L).build());
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void updateComment() {
        setUserProv();
        when(commentRepository.findByCid(anyLong())).thenReturn(
                Optional.of(CommentEntity.builder().uid(userEntity).build()));

        Long result = commentService.updateComment(CommentDto.builder().cid(1L).build());
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void saveCommentLikes() {
        setUserProv();
        when(commentRepository.findByCid(anyLong())).thenReturn(Optional.of(CommentEntity.builder().build()));
        when(commentLikeRepository.findByCidAndUid(any(CommentEntity.class), any(UserEntity.class))).thenReturn(
                Optional.empty());
        when(commentLikeConverter.toEntity(any(CommentLikeDto.class), any(UserEntity.class),
                any(CommentEntity.class))).thenReturn(CommentLikeEntity.builder().build());
        when(commentLikeRepository.save(any(CommentLikeEntity.class))).thenReturn(null);

        Integer result = commentService.saveCommentLike(CommentLikeDto.builder().cid(1L).build());
        Assertions.assertThat(result).isEqualTo(201);
        verify(commentLikeRepository, times(1)).save(any(CommentLikeEntity.class));
    }

    @Test
    void deleteComment() {
        setUserProv();
        when(commentRepository.findByCid(anyLong())).thenReturn(
                Optional.of(CommentEntity.builder().uid(userEntity).build()));

        Long result = commentService.deleteComment(1L);
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void deleteCommentLike() {
        setUserProv();
        when(commentRepository.findByCid(anyLong())).thenReturn(Optional.of(CommentEntity.builder().build()));
        when(commentLikeRepository.findByCidAndUid(any(CommentEntity.class), any(UserEntity.class))).thenReturn(
                Optional.of(CommentLikeEntity.builder().build()));
        doNothing().when(commentLikeRepository).delete(any(CommentLikeEntity.class));

        Long result = commentService.deleteCommentLike(1L);
        Assertions.assertThat(result).isEqualTo(1L);
        verify(commentLikeRepository, times(1)).delete(any(CommentLikeEntity.class));
    }

    private void setUserContextByUsername() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user", null, null));
    }

    private void setUserProv() {
        setUserContextByUsername();
        when(userRepository.findByProvider(anyString())).thenReturn(
                Optional.of(userEntity));
    }

}
