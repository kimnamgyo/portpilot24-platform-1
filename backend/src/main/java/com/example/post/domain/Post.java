package com.example.post.domain;

import java.time.LocalDateTime;

import com.example.post.dto.postDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue
    private Long post_id;

    @Column
    private Long user_id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Boolean is_Notice;

    @Column
    private LocalDateTime created_At;

    @Column
    private LocalDateTime updated_At;

    @Column
    private int fileAttached;

    public static Post noFilePost(postDTO postDTO){
        Post post = new Post();

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setIs_Notice(postDTO.getIs_Notice());
        post.setCreated_At(postDTO.getCreated_At());
        post.setUpdated_At(post.getUpdated_At());
        
        return post;
    }

    public static Post filePost(postDTO postDTO){
        Post post = new Post();
        
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setIs_Notice(postDTO.getIs_Notice());
        post.setCreated_At(postDTO.getCreated_At());
        post.setUpdated_At(post.getUpdated_At());

        //파일 업로드 유무
        post.setFileAttached(post.getFileAttached());

        return post;
        
    }


    
}