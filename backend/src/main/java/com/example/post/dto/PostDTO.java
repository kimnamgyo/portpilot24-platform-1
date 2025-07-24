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
        postDTO.setPost_id(postEntity.getPostId());
        postDTO.setUser_id(postEntity.getUserId());
        postDTO.setName(postEntity.getName());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setIs_Notice(postEntity.getIsNotice());
        postDTO.setCreated_At(postEntity.getCreatedAt());
        postDTO.setUpdated_At(postEntity.getUpdatedAt());

        if(postEntity.getFileAttached() == 0){
            postDTO.setFileAttached(postEntity.getFileAttached());
        }else{
            postDTO.setFileAttached(postEntity.getFileAttached());
            postDTO.setOriginalFileName(postEntity.getPostFileEntityList().get(0).getOriginalFileName());
            postDTO.setServerFileName(postEntity.getPostFileEntityList().get(0).getSavedFileName());
        }
        return postDTO;
    }

    // 게시글 목록에서 필요한 정보를 DTO에 담기위한 생성자
    public PostDTO(Long postid, String name, String title, LocalDateTime createAt){
        this.post_id = postid;
        this.name = name;
        this.title = title;
        this.created_At = createAt;
    }
    
}
