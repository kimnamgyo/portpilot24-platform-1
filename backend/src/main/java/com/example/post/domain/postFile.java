package com.example.post.domain;

import java.time.LocalDateTime;

import com.example.post.dto.postDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="postFile")
public class postFile extends Post {

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
    private Post post;

    public static postFile postFile(postDTO postDTO){
        postFile postFile = new postFile();

        postFile.setOriginalFileName(postDTO.getOriginalFileName());
        postFile.setSavedFileName(postDTO.getServerFileName());
        postFile.setCreated_At(postDTO.getCreated_At());
        postFile.setUpodated_At(postDTO.getUpdated_At());

        return postFile;
    }
    
    public static postFile toPostFile(Post post, String originalFileName, String savedFileName) {
        Post toPostFile = new postFile();
        toPostFile.setOriginalFileName(originalFileName);
        toPostFile.setSavedFileName(savedFileName);
        toPostFile.setPost(post);
    return post;
    }
    
}
