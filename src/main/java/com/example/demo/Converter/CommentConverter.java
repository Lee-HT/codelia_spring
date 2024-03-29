package com.example.demo.Converter;

import com.example.demo.DTO.CommentDto;
import com.example.demo.DTO.CommentPageDto;
import com.example.demo.Entity.CommentEntity;
import com.example.demo.Entity.PostEntity;
import com.example.demo.Entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentConverter {

    public CommentEntity toEntity(CommentDto commentDto, UserEntity userEntity,
            PostEntity postEntity) {
        return CommentEntity.builder().cid(commentDto.getCid()).uid(userEntity).pid(postEntity)
                .username(commentDto.getUsername())
                .contents(commentDto.getContents()).build();
    }

    public List<CommentEntity> toEntity(List<CommentDto> commentDto, List<UserEntity> userEntity,
            List<PostEntity> postEntity) {
        List<CommentEntity> commentEntity = new ArrayList<>();
        for (int i = 0; i < commentDto.size(); i++) {
            commentEntity.add(toEntity(commentDto.get(i), userEntity.get(i), postEntity.get(i)));
        }
        return commentEntity;
    }

    public CommentDto toDto(CommentEntity comment) {
        try {
            return CommentDto.builder().cid(comment.getCid()).uid(comment.getUid().getUid())
                    .pid(comment.getPid().getPid()).username(comment.getUsername())
                    .contents(comment.getContents()).build();
        } catch (Exception e) {
            log.info(e.toString());
        }
        return CommentDto.builder().build();
    }

    public List<CommentDto> toDto(List<CommentEntity> comment) {
        List<CommentDto> commentDto = new ArrayList<>();
        for (CommentEntity ett : comment) {
            commentDto.add(toDto(ett));
        }
        return commentDto;
    }

    public CommentPageDto toDto(Page<CommentEntity> pages) {
        try {
            return CommentPageDto.builder().contents(toDto(pages.getContent()))
                    .totalPages(pages.getTotalPages()).size(pages.getSize())
                    .numberOfElements(pages.getNumberOfElements())
                    .totalElements(pages.getTotalElements()).sorted(pages.getSort().isSorted()).build();
        } catch (Exception e) {
            log.info(e.toString());
        }
        return CommentPageDto.builder().build();
    }


}
