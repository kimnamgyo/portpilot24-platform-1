package com.example.post.service;

import java.util.List;

import com.example.post.domain.Post;

public interface postService {

    //게시글 등록기능(create)
    Post insertPost(Post post);

    //전체 게시글 조회(research_all)
    List<Post> findPosts();
    
    //특정 게시글 조회(research_post)
    Post findPost(Long id);

    //게시글 수정
    Post updatePost(Long id, Post post);

    //게시글 삭제
    void deletePost(Long id);

    
}
