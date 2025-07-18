package com.example.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.post.domain.Post;
import com.example.post.repository.postRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class postServiceImpl implements postService {

    private final postRepository postRepository;

    // 게시글 등록
    @Override
    public Post insertPost(Post post) {
        return postRepository.save(post);
    }

    //전체 게시글 조회
    @Override
    public List<Post> findPosts() {
        return postRepository.findAll();
    }

    //특정 게시글 조회
    @Override
    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }

    //게시글 수정
    @Override
    public Post updatePost(Long id, Post post) {
        
        Post fixedPost = findPost(id);
        fixedPost.setTitle(post.getTitle());
        fixedPost.setContent(post.getContent());
        fixedPost.setUpdated_At(post.getUpdated_At());

        return postRepository.save(fixedPost);
    }

    //게시글 삭제
    @Override
    public void deletePost(Long id) {
        deletePost(id);
    }
    
}
