package com.example.post.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
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

    
}