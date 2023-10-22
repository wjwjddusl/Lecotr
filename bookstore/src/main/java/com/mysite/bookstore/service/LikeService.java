package com.mysite.bookstore.service;

import com.mysite.bookstore.dto.LikeDTO;
import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardLike;
import com.mysite.bookstore.entity.UserEntity;
import com.mysite.bookstore.repository.BoardRepository;
import com.mysite.bookstore.repository.LikeRepository;
import com.mysite.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public ResponseEntity addLikes(Long id, String username, LikeDTO likeDTO) throws Exception{
        UserEntity user = userRepository.findByUsername(username);
        BoardLike boardLike = new BoardLike();
        Optional<BoardEntity> board= boardRepository.findById(id);

        likeDTO.setBoard(board.get()); //게시글 번호 저장
        likeDTO.setUser(user); //유저 번호 저장
        likeDTO.setSearchBoardId(id); //검색을 위한 Long 타입 게시글 번호 저장

        if(!likeRepository.findBoardLikeByUserAndBoard(likeDTO.getUser(),likeDTO.getBoard()).isPresent()){
            //관심 누르면
            likeRepository.save(boardLike.toLikeEntity(likeDTO)); //저장
        }else{
            //두번 누르면 취소되도록 함.
            likeRepository.deleteByBoardAndUser(likeDTO.getBoard(),likeDTO.getUser());
            System.out.println("delete 성공");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
