package com.example.post.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.PostEntity;
import com.example.post.domain.PostFileEntity;
import com.example.post.dto.PostDTO;
import com.example.post.repository.postFileRepository;
import com.example.post.repository.postRepository;

import jakarta.mail.Multipart;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final postRepository postRepository;
    private final postFileRepository postFileRepository;

    // 게시글 등록
    @Override
    public void insertPost(PostDTO postDTO) throws IOException  {

        if(postDTO.getPostFile().isEmpty()){
            PostEntity post = PostEntity.noFilePostEntity(postDTO);
            postRepository.save(post);
        }else{
            MultipartFile postedFile = postDTO.getPostFile();

            String originalFilename = postedFile.getOriginalFilename();

            String savedFileName = System.currentTimeMillis() + "_" + originalFilename;

            String savePath = "C:/springboot_img/" + savedFileName;

            postedFile.transferTo(new File(savePath));

            PostEntity post = PostEntity.filePostEntity(postDTO);

            // Long savedId = postRepository.save(post).getPost_id();
            // PostEntity postInDB = postRepository.findById(savedId).get();

            // PostFileEntity postFileEntity = PostFileEntity.toPostFile(postInDB, originalFilename, savedFileName);
            
            // postRepository.save(post);
            // postFileRepository.save(postFileEntity);
            
        } 
    }

    // @Transactional
    // public PostDTO findById(Long id) {
    //     Optional<Post> optionalPostEntity = postRepository.findById(id);
    //     if (optionalPostEntity.isPresent()) {
    //         PostEntity post = optionalPostEntity.get();
    //         PostDTO postToDTO = postDTO.toPostDTO(post);
    //         return postToDTO;
    //     } else {
    //         return null;
    //     }
    // }

    // @Transactional
    // public List<postDTO> findAll() {

    //     List<Post> postEntityList = postRepository.findAll();

    //     List<postDTO> boardDTOList = new ArrayList<>();
    //     for (Post boardEntity : postEntityList) {
    //         boardDTOList.add(postDTO.toPostDTO(boardEntity));
    //     }

    //     return boardDTOList;
    // }

    //전체 게시글 조회
    @Override
    public List<PostEntity> findPosts() {
        return postRepository.findAll();
    }

    //특정 게시글 조회
    @Override
    public PostEntity findPost(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }

    //게시글 수정
    @Override
    public PostEntity updatePost(Long id, PostEntity post) {
        
        PostEntity fixedPost = findPost(id);
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
