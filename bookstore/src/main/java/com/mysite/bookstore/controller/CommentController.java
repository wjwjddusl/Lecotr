package com.mysite.bookstore.controller;

import com.mysite.bookstore.dto.CommentDTO;
import com.mysite.bookstore.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final HttpSession session;

    @PostMapping("/comment/save")
    public ResponseEntity save(@ModelAttribute CommentDTO commentDTO) {
        //System.out.println("commentDTO = " + commentDTO);
        String username= String.valueOf(session.getAttribute("loginUsername")); //세션 유저 이름 가져오기
        Long saveResult = commentService.save(commentDTO,username);
        if (saveResult != null) {
            //작성 성공, 댓글목록(해당 게시글) 화면에 보여주기(게시글 아이디로 검색)
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());

            return new ResponseEntity<>(commentDTOList, HttpStatus.OK);//데이터와 함께 상태코드 함꼐 전달
        } else {
            return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
            //ajax 콘솔 메시지 보여줌
        }
    }

    //댓글 삭제
    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity deleteById(@PathVariable ("id") Long comment_id){
        System.out.println("comment_id = " + comment_id);
        commentService.deleteById(comment_id);

        return  new ResponseEntity(HttpStatus.OK);
    }


}
