package com.example.demo.Service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.Converter.PostConverter;
import com.example.demo.DTO.LikeDto;
import com.example.demo.DTO.PostDto;
import com.example.demo.DTO.PostLikeDto;
import com.example.demo.DTO.PostPageDto;
import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.PostLikeEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.PostLikeRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.Impl.PostServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostConverter postConverter;
    @InjectMocks
    private PostServiceImpl postService;
    private List<PostEntity> posts = new ArrayList<>();
    private List<UserEntity> users = new ArrayList<>();
    private List<PostDto> postDtos = new ArrayList<>();
    private List<PostLikeEntity> postLikes = new ArrayList<>();
    private Pageable pageable = PageRequest.of(0, 3, Direction.DESC, "pid");
    private int maxIdx;

    @Autowired
    public PostServiceTest() {
        for (int i = 1; i < 3; i++) {
            users.add(UserEntity.builder().uid((long) i).username("user" + i)
                    .email("email" + i + "@gmail.com").build());
        }
        for (int i = 1; i < 6; i++) {
            posts.add(PostEntity.builder().pid((long) i).uid(users.get(0)).title("title" + i)
                    .contents("contents" + i).build());
            postDtos.add(PostDto.builder().pid((long) i).title("title" + i).contents("contents" + i)
                    .build());
        }
        for (int i = 0; i < 5; i++) {
            postLikes.add(
                    PostLikeEntity.builder().pid(posts.get(i)).uid(users.get(i / 3))
                            .likes(i % 2 == 0).build());
        }
        this.maxIdx = posts.size();
    }

    private void SetUserContextByUsername() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user", null, null));
    }

    private void SetUserProv() {
        SetUserContextByUsername();
        when(userRepository.findByProvider(anyString())).thenReturn(Optional.of(users.get(0)));
    }

    @Test
    @DisplayName("POST PAGE SELECT")
    public void findPostPage() {
        System.out.println("======== findPostPage ========");
        PostPageDto pageDto = PostPageDto.builder().build();
        when(postRepository.findAll(this.pageable)).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(postConverter.toDto(ArgumentMatchers.<Page<PostEntity>>any())).thenReturn(pageDto);
        PostPageDto result = postService.findPostPage(this.pageable);

        Assertions.assertThat(result).isEqualTo(pageDto);

        System.out.println(result);
    }

    @Test
    @DisplayName("PID 기준 SELECT POST")
    public void findPost() {
        System.out.println("======== findByPid ========");

        when(postRepository.findByPid(any(Long.class))).thenReturn(Optional.of(this.posts.get(0)));
        when(postConverter.toDto(any(PostEntity.class))).thenReturn(this.postDtos.get(0));
        PostDto result = postService.findPost(1L);

        Assertions.assertThat(result).isEqualTo(this.postDtos.get(0));

        System.out.println(result);
    }

    @Test
    @DisplayName("TITLE 기준 SELECT POST")
    public void findPostByTitle() {
        System.out.println("======== findByTitleContaining ========");
        when(postRepository.findByTitleContaining(anyString(), eq(this.pageable))).thenReturn(
                new PageImpl<>(new ArrayList<>()));
        when(postConverter.toDto(ArgumentMatchers.<Page<PostEntity>>any())).thenReturn(
                PostPageDto.builder().build());
        PostPageDto result = postService.findPostByTitle("title", pageable);

        Assertions.assertThat(result).isInstanceOf(PostPageDto.class);

        System.out.println(result);
    }

    @Test
    @DisplayName("USERNAME 기준 SELECT POST")
    public void findPostByUsername() {
        System.out.println("======== findPostByUsername ========");

        when(postRepository.findByUsernameContaining(anyString(), eq(this.pageable))).thenReturn(
                new PageImpl<>(new ArrayList<>()));
        when(postConverter.toDto(ArgumentMatchers.<Page<PostEntity>>any())).thenReturn(
                PostPageDto.builder()
                        .build());
        PostPageDto result = postService.findPostByUsername("user1", this.pageable);

        Assertions.assertThat(result).isInstanceOf(PostPageDto.class);

        System.out.println(posts);
    }

    @Test
    @DisplayName("SAVE POST")
    public void savePost() {
        System.out.println("======== savePost ========");
        SetUserProv();
        when(postConverter.toEntity(any(PostDto.class), any(UserEntity.class))).thenReturn(
                this.posts.get(1));
        when(postRepository.save(any(PostEntity.class))).thenReturn(this.posts.get(1));
        when(postConverter.toDto(any(PostEntity.class))).thenReturn(postDtos.get(0));
        PostDto result = postService.savePost(postDtos.get(0));

        PostDto postDto = PostDto.builder().pid(1L).title("title1").contents("contents1").build();
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(postDto);

        System.out.println(result);
    }

    @Test
    @DisplayName("UPDATE POST")
    public void updatePost() {
        System.out.println("======== updatePost ========");
        SetUserProv();
        PostDto postDto = PostDto.builder().pid(1L).title("title3").contents("contents3").build();
        PostEntity postEntity = PostEntity.builder().pid(1L).uid(users.get(0)).title("title1")
                .contents("contents1")
                .build();
        when(postRepository.findByPid(anyLong())).thenReturn(Optional.of(postEntity));
        when(postConverter.toDto(any(PostEntity.class))).thenReturn(postDto);
        PostDto result = postService.updatePost(postDto);

        Assertions.assertThat(result).isEqualTo(postDto);

        System.out.println(result);
    }

    @Test
    @DisplayName("DELETE POST")
    public void deletePost() {
        System.out.println("======== deletePost ========");
        SetUserProv();
        when(postRepository.findByPid(anyLong())).thenReturn(Optional.of(posts.get(0)));
        doNothing().when(postRepository).delete(any(PostEntity.class));

        Long result = postService.deletePost(1L);

        Assertions.assertThat(result).isEqualTo(1L);
        verify(postRepository, times(1)).delete(any(PostEntity.class));

        System.out.println(result);
    }

    @Test
    @DisplayName("DELETE POSTS")
    public void deletePosts() {
        System.out.println("======== deletePosts ========");
        SetUserProv();

        List<Long> pid = Arrays.asList(1L, 2L);
        when(postRepository.findByPid(1L)).thenReturn(Optional.of(posts.get(0)));
        when(postRepository.findByPid(2L)).thenReturn(Optional.of(posts.get(1)));
        int count = postService.deletePosts(pid);

        Assertions.assertThat(count).isEqualTo(2);

        System.out.println(count);
    }

    @Test
    @DisplayName("POST 좋아요")
    public void likesPost() {
        System.out.println("======== likesPost ========");
        SetUserProv();
        PostLikeDto dto = PostLikeDto.builder().pid(posts.get(0).getPid())
                .uid(users.get(0).getUid()).likes(false).build();
        when(postRepository.findByPid(any(Long.class))).thenReturn(Optional.of(posts.get(0)));
        when(postLikeRepository.findByPidAndUid(posts.get(0), users.get(0))).thenReturn(
                Optional.of(this.postLikes.get(0)));
        LikeDto result = postService.setlikeState(dto);

        Assertions.assertThat(result.getLikes()).isEqualTo(false);

        System.out.println(result);
    }

}