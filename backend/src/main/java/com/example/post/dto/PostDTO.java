package com.example.post.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long postId;
    private Long userId;
    private String name;
    private String title;
    private String content;
    private Boolean isNotice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 파일업로드는 따로 뺄것
    // private MultipartFile postFile;
    // private String originalFileName;
    // private String serverFileName;
    // private int fileAttached;

    // 엔티티의 데이터를 DTO에 넣을때 사용할 함수
    public static PostDTO toPostDTO(PostEntity postEntity){
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(postEntity.getPostId());
        postDTO.setUserId(postEntity.getUserId());
        postDTO.setName(postEntity.getName());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setIsNotice(postEntity.getIsNotice());
        postDTO.setCreatedAt(postEntity.getCreatedAt());
        postDTO.setUpdatedAt(postEntity.getUpdatedAt());

        // 첨부파일 유무를 확인하던 함수.
        // if(postEntity.getFileAttached() == 0){
        //     postDTO.setFileAttached(postEntity.getFileAttached());
        // }else{
        //     postDTO.setFileAttached(postEntity.getFileAttached());
        //     postDTO.setOriginalFileName(postEntity.getPostFileEntityList().get(0).getOriginalFileName());
        //     postDTO.setServerFileName(postEntity.getPostFileEntityList().get(0).getSavedFileName());
        // }

        return postDTO;
    }

    
}
