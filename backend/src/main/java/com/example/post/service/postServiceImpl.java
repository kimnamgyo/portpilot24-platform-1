package com.example.post.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.Post;
import com.example.post.domain.postFile;
import com.example.post.dto.postDTO;
import com.example.post.repository.postFileRepository;
import com.example.post.repository.postRepository;

import jakarta.mail.Multipart;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class postServiceImpl implements postService {

    private final postRepository postRepository;
    private final postFileRepository postFileRepository;

    // 게시글 등록
    @Override
    public void insertPost(postDTO postDTO) throws IOException  {

        if(postDTO.getPostFile().isEmpty()){
            Post post = Post.noFilePost(postDTO);
            postRepository.save(post);
        }else{
            MultipartFile postFile = postDTO.getPostFile();

            String originalFilename = postFile.getOriginalFilename();

            String savedFileName = System.currentTimeMillis() + "_" + originalFilename;

            String savePath = "C:/springboot_img/" + savedFileName;

            postFile.transferTo(new File(savePath));

            Post post = Post.filePost(postDTO);

            Long savedId = postRepository.save(post).getPost_id();
            Post postInDB = postRepository.findById(savedId).get();

            postFile postFileEntity = postFile.toPostFile(postInDB, originalFilename, savedFileName);
            
            postRepository.save(post);
            postFileRepository.save(postFileEntity);
            
        } 
    }

    @Transactional
    public postDTO findById(Long id) {
        Optional<Post> optionalPostEntity = postRepository.findById(id);
        if (optionalPostEntity.isPresent()) {
            Post post = optionalPostEntity.get();
            postDTO postDTO = postDTO.toPostDTO(post);
            return postDTO;
        } else {
            return null;
        }
    }

    @Transactional
    public List<postDTO> findAll() {

        List<Post> postEntityList = postRepository.findAll();

        List<postDTO> boardDTOList = new ArrayList<>();
        for (Post boardEntity : postEntityList) {
            boardDTOList.add(postDTO.toPostDTO(boardEntity));
        }

        return boardDTOList;
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
