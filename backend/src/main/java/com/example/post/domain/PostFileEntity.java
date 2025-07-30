package com.example.post.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.post.dto.PostDTO;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
    private String originalFileName;

    @Column
    private String savedFileName;

    @ElementCollection
    private List<String> filePaths;
    
    // 파일을 서버 로컬에 저장할 경우 35~36라인 제거
    // @Column
    // private MultipartFile file;

    // @ManyToOne
    // @JoinColumn(name = "post_id", nullable = false)
    // private PostEntity postEntity;

    // 파일에 대한 정보를 담는 DB // 파일 자체는 서버의 로컬공간에 따로 저장
    // 데이터베이스로 들어갈 정보들을 위한 객체 생성 함수
    // public static PostFileEntity toPostFileEntity(PostDTO postDTO){
    //     PostFileEntity postFileEntity = new PostFileEntity();

    //     postFileEntity.setOriginalFileName(postDTO.getOriginalFileName());
    //     postFileEntity.setSavedFileName(postDTO.getServerFileName());
    //     postFileEntity.setCreatedAt(postDTO.getCreatedAt());
    //     postFileEntity.setUpodatedAt(postDTO.getUpdatedAt());

    //     return postFileEntity;
    // }
    
    // // 데이터베이스로 부터 게시글 엔티티를 가져와서 파일 정보가 들어간 새로운 엔티티 생성.
    // public static PostFileEntity toPostFileEntity(PostEntity postEntity, String originalFileName, String savedFileName) {
    //     PostFileEntity toPostFileEntity = new PostFileEntity();
    //     toPostFileEntity.setOriginalFileName(originalFileName);
    //     toPostFileEntity.setSavedFileName(savedFileName);
    //     toPostFileEntity.setPostEntity(postEntity);
    // return toPostFileEntity;
    // }
    
}
