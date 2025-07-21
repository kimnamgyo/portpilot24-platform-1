package com.example.post.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.Post;
import com.example.post.dto.*;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Setter
public class postDTO {

    private Long post_id;
    private Long user_id;
    private String title;
    private String content;
    private Boolean is_Notice;
    private LocalDateTime created_At;
    private LocalDateTime updated_At;


    private MultipartFile postFile;
    private String originalFileName;
    private String serverFileName;
    private int fileAttached;



    public static postDTO toPostDTO(Post post){
        postDTO postDTO = new postDTO();
        postDTO.setPost_id(post.getPost_id());
        postDTO.setUser_id(post.getUser_id());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setIs_Notice(post.getIs_Notice());
        postDTO.setCreated_At(post.getCreated_At());
        postDTO.setUpdated_At(post.getUpdated_At());

        if(post.getFileAttached() == 0){
            postDTO.setFileAttached(post.getFileAttached());
        }else{
            postDTO.setFileAttached(post.getFileAttached());
        }

        return postDTO;
    }

}
