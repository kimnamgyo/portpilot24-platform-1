package com.example.post.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
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

    //페이징처리
    @GetMapping("/paging")
    public Page<PostDTO> paging(@PageableDefault(page = 1) Pageable pageable, Model model){

        Page<PostDTO> postList = postService.paging(pageable);

        int pageLimit = 10;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / pageLimit))) - 1) * pageLimit + 1;
        int endPage = ((startPage + pageLimit - 1) < postList.getTotalPages()) ? startPage + pageLimit - 1 : postList.getTotalPages();

        model.addAttribute("postList", postList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return postList;
    }

    //특정게시물조회
    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id){
        return postService.findPost(id);
    }
    
    //게시글등록
    @PostMapping
    public void insertPost(@RequestBody PostDTO postDTO) throws IOException{
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
