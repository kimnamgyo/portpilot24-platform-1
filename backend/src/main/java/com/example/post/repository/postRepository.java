package com.example.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.post.domain.Post;

@Repository
public interface postRepository extends JpaRepository<Post, Long> {
    
}
