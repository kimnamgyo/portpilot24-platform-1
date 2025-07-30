package com.example.post.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.PostEntity;
import com.example.post.dto.PostDTO;

// import jakarta.mail.Multipart;

public interface PostService {

    //게시글 등록기능(create)
    // PostEntity insertPost(PostDTO postDTO) throws IOException;
    void insertPost(String title, String content) throws IOException;

    //파일 업로드 기능
    void uploadFile(List<MultipartFile> files) throws IOException;

    //전체 게시글 조회(research_all)
    List<PostDTO> findPosts();
    
    //페이징 처리
    Page<PostEntity> paging(Pageable pageable);
    
    //특정 게시글 조회(research_post)
    PostDTO findPost(Long id);

    //게시글 수정
    void updatePost(Long id, PostDTO post) throws IOException;

    //게시글 삭제
    void deletePost(Long id);



    
}
