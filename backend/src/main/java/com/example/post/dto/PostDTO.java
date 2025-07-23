package com.example.post.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.PostEntity;

import lombok.Data;

@Data
public class PostDTO {

    private Long post_id;
    private Long user_id;
    private String name;
    private String title;
    private String content;
    private Boolean is_Notice;
    private LocalDateTime created_At;
    private LocalDateTime updated_At;


    private MultipartFile postFile;
    private String originalFileName;
    private String serverFileName;
    private int fileAttached;

    // 엔티티의 데이터를 DTO에 넣을때 사용할 함수
    public static PostDTO toPostDTO(PostEntity postEntity){
        PostDTO postDTO = new PostDTO();
        postDTO.setPost_id(postEntity.getPost_id());
        postDTO.setUser_id(postEntity.getUser_id());
        postDTO.setName(postEntity.getName());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setIs_Notice(postEntity.getIs_Notice());
        postDTO.setCreated_At(postEntity.getCreated_At());
        postDTO.setUpdated_At(postEntity.getUpdated_At());

        if(postEntity.getFileAttached() == 0){
            postDTO.setFileAttached(postEntity.getFileAttached());
        }else{
            postDTO.setFileAttached(postEntity.getFileAttached());
            postDTO.setOriginalFileName(postEntity.getPostFileEntityList().get(0).getOriginalFileName());
            postDTO.setServerFileName(postEntity.getPostFileEntityList().get(0).getSavedFileName());
        }
        return postDTO;
    }

}
