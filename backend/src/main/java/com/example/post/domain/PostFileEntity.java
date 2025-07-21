package com.example.post.domain;

import java.time.LocalDateTime;

import com.example.post.dto.PostDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="postFile")
public class PostFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column
    private LocalDateTime created_At;

    @Column
    private LocalDateTime upodated_At;

    @Column
    private String originalFileName;

    @Column
    private String savedFileName;

    @ManyToOne
    @JoinColumn
    private PostEntity postEntity;

    // 파일에 대한 정보를 담는 DB // 파일 자체는 서버의 로컬공간에 따로 저장
    public static PostFileEntity postFileEntity(PostDTO postDTO){
        PostFileEntity postFileEntity = new PostFileEntity();

        postFileEntity.setOriginalFileName(postDTO.getOriginalFileName());
        postFileEntity.setSavedFileName(postDTO.getServerFileName());
        postFileEntity.setCreated_At(postDTO.getCreated_At());
        postFileEntity.setUpodated_At(postDTO.getUpdated_At());

        return postFileEntity;
    }
    
    // public static PostEntity toPostFile(Post post, String originalFileName, String savedFileName) {
    //     PostEntity toPostFile = new PostFileEntity();
    //     toPostFile.setOriginalFileName(originalFileName);
    //     toPostFile.setSavedFileName(savedFileName);
    //     toPostFile.setPost(post);
    // return post;
    // }
    
}
