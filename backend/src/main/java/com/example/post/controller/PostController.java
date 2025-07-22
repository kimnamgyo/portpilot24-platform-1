package com.example.post.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.post.dto.PostDTO;
import com.example.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    //전체조회
    @GetMapping
    public List<PostDTO> getPost(){
        return postService.findPosts();
    }

    //특정게시물조회
    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id){
        return postService.findPost(id);
    }
    
    //게시글등록
    @PostMapping
    public void insertPost(PostDTO postDTO) throws IOException{
        postService.insertPost(postDTO);
    }

    //게시글수정
    @PatchMapping("/{id}")
    public void updatePost(@PathVariable Long id, @RequestBody PostDTO post) throws IOException{
        postService.updatePost(id, post);
    }

    //게시글삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        postService.deletePost(id);
    }
}
