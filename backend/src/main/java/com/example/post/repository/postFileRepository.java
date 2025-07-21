package com.example.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.post.domain.Post;
import com.example.post.domain.postFile;

@Repository
public interface postFileRepository extends JpaRepository<postFile, Long> {
    
}
