package com.example.post.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.post.domain.PostEntity;
// import com.example.post.domain.PostFileEntity;
import com.example.post.dto.PostDTO;
import com.example.post.repository.postPagingRepository;
// import com.example.post.repository.postFileRepository;
import com.example.post.repository.postRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final postRepository postRepository;
    private final postPagingRepository postPagingRepository;
    // private final postFileRepository postFileRepository;

    // 게시글 등록(파일첨부 기능을 따로 빼기 전)
    // @Override
    // public void insertPost(PostDTO postDTO) throws IOException  {

    //     MultipartFile postFile = postDTO.getPostFile();

    //     if (postFile != null && !postFile.isEmpty()) {
    //         // PostDTO에서 파일 꺼내 변수에 저장
    //         MultipartFile postedFile = postDTO.getPostFile();

    //         // 파일에서 이름을 꺼내서 담아줌.
    //         String originalFilename = postedFile.getOriginalFilename();

    //         // 서버에 저장할 이름을 따로 지정
    //         String savedFileName = System.currentTimeMillis() + "_" + originalFilename;

    //         // 파일 자체는 데이터베이스 내가 아닌 서버의 로컬 공간에 저장
    //         // 파일이 들어갈 경로를 저장
    //         String savePath = "C:/springboot_file/" + savedFileName;
    //         // 파일 저장
    //         postedFile.transferTo(new File(savePath));

    //         // 파일 정보를 제외한 게시글 저장
    //         PostEntity filePost = PostEntity.filePostEntity(postDTO);
    //         postRepository.save(filePost);

    //         Long saveId = postRepository.save(filePost).getPostId();
    //         PostEntity post = postRepository.findById(saveId).get(); // 첨부된 파일이 들어간 게시글 엔티티를 받아옴.

    //         PostFileEntity postFileEntity = PostFileEntity.toPostFileEntity(post, originalFilename, savedFileName);
    //         postFileRepository.save(postFileEntity);

    //     }else{
    //         PostEntity post = PostEntity.noFilePostEntity(postDTO);
    //         postRepository.save(post);
    //     } 
    // }

    //게시글 등록
    @Override
    public void insertPost(PostDTO postDTO) throws IOException {
        PostEntity post = PostEntity.noFilePostEntity(postDTO);
        postRepository.save(post);
    }

    //파일 업로드 기능(아직 로직 추가 안함)
    @Override
    public void uploadFile(MultipartFile file, String name) throws IOException {

            // 서버에 저장할 이름을 따로 지정
            String savedFileName = System.currentTimeMillis() + "_" + name;

            // 파일이 데이터베이스 내가 아닌 서버의 로컬 공간에 저장할 경우 사용(db에 집어넣을거면 85~89라인은 없어도 됩니다!)
            // 파일이 들어갈 경로를 저장
            String savePath = "C:/springboot_file/" + savedFileName;
            // 파일 저장
            file.transferTo(new File(savePath));

            // hibernate가 multipartfile 타입의 데이터를 자동으로 다뤄주지 않음....
            // 파일을 db에 저장할 경우(로컬에 저장할 생각이라면 92~97라인은 없어도 됩니다! 방식은 나중에 생각하는걸로....)
            // PostFileEntity postFileEntity = new PostFileEntity();
            // postFileEntity.setFile(file);
            // postFileEntity.setOriginalFileName(name);
            // postFileEntity.setSavedFileName(savedFileName);

            // postFileRepository.save(postFileEntity);

    }
    
    //전체 게시글 조회
    @Override
    @Transactional
    public List<PostDTO> findPosts() {

        List<PostEntity> postEntityList = postRepository.findAll();
        List<PostDTO> postDTOList = new ArrayList<>();
        for (PostEntity postEntity : postEntityList) {
            postDTOList.add(PostDTO.toPostDTO(postEntity));
        }
        return postDTOList;
    }

    //페이징처리
    @Override
    public Page<PostEntity> paging(Pageable pageable) {

        
        return postPagingRepository.findAll(pageable);
    }

    //특정 게시글 조회
    @Override
    @Transactional
    public PostDTO findPost(Long id) {
        Optional<PostEntity> optionalPostEntity = postRepository.findById(id);
        if (optionalPostEntity.isPresent()) {
            PostEntity post = optionalPostEntity.get();
            PostDTO postToDTO = PostDTO.toPostDTO(post);
            return postToDTO;
        } else {
            return null;
        }

        // return postRepository.findById(id).orElseThrow(
        //         () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
        // );
    }

    //게시글 수정
    @Override
    public void updatePost(Long id, PostDTO post) throws IOException {
        
        PostDTO fixedPost = findPost(id);
        fixedPost.setTitle(post.getTitle());
        fixedPost.setContent(post.getContent());
        fixedPost.setUpdatedAt(post.getUpdatedAt());

        PostEntity postEntity = new PostEntity();
        postEntity = postRepository.findById(id).get();
        postEntity.setTitle(fixedPost.getTitle());
        postEntity.setContent(fixedPost.getContent());
        postEntity.setUpdatedAt(fixedPost.getUpdatedAt());
        postRepository.save(postEntity);

        
    }

    //게시글 삭제
    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }


}
