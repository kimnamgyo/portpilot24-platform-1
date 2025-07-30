package com.example.post.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
        private Long postId;
        private String name;
        private String title;
        private LocalDateTime createdAt;

        // 게시글 목록에서 필요한 정보를 DTO에 담기위한 생성자
        // public Page<ResponseDTO> responseDTO(Page<PostEntity> postEntity){
        //     Page<ResponseDTO> responseDto;
        //     responseDto.setPostId(postEntity.getPostId());
        //     responseDto.setName(postEntity.getName());
        //     responseDto.setTitle(postEntity.getTitle());
        //     responseDto.setCreatedAt(postEntity.getCreatedAt());
        //     return responseDto;
        // }
        
}
