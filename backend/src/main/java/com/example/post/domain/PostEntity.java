package com.example.post.domain;

import java.time.LocalDateTime;

import com.example.post.dto.PostDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="post")
public class PostEntity {

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

    //첨부 파일이 없는경우 게시글에 사용되는 데이터만 담음.
    public static PostEntity noFilePostEntity(PostDTO postDTO){
        PostEntity noFilePostEntity = new PostEntity();

        noFilePostEntity.setTitle(postDTO.getTitle());
        noFilePostEntity.setContent(postDTO.getContent());
        noFilePostEntity.setIs_Notice(postDTO.getIs_Notice());
        noFilePostEntity.setCreated_At(postDTO.getCreated_At());
        noFilePostEntity.setUpdated_At(noFilePostEntity.getUpdated_At());
        
        return noFilePostEntity;
    }

    //첨부 파일이 있는 경우 첨부 파일이 있는지 유무를 확인하는 변수를 추가로 담음
    public static PostEntity filePostEntity(PostDTO postDTO){
        PostEntity filePostEntity = new PostEntity();
        
        filePostEntity.setTitle(postDTO.getTitle());
        filePostEntity.setContent(postDTO.getContent());
        filePostEntity.setIs_Notice(postDTO.getIs_Notice());
        filePostEntity.setCreated_At(postDTO.getCreated_At());
        filePostEntity.setUpdated_At(filePostEntity.getUpdated_At());

        //파일 업로드 유무
        filePostEntity.setFileAttached(filePostEntity.getFileAttached());

        return filePostEntity;
        
    }


    
}