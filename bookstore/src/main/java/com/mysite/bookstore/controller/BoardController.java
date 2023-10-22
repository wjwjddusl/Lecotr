package com.mysite.bookstore.controller;

import com.mysite.bookstore.dto.BoardDTO;
import com.mysite.bookstore.dto.CommentDTO;
import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.UserEntity;
import com.mysite.bookstore.service.BoardService;
import com.mysite.bookstore.service.CommentService;
import com.mysite.bookstore.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor //서비스 클래스 의존성 주입을 위함
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final HttpSession session;

    @GetMapping("/")
    public String index(@PageableDefault(page=1)Pageable pageable, Model model){
        Page<BoardDTO> boardList = boardService.paging_index(pageable);
        model.addAttribute("boardList", boardList);
        return "index";
    }

    @GetMapping("/board/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/board/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        //System.out.println("boardDTO = " + boardDTO);
        String username= String.valueOf(session.getAttribute("loginUsername"));
        boardService.save(boardDTO, username); //세션에 저장된 username 전달
        return "redirect:/board/paging";
    }

    @GetMapping("/board/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
         */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        //관심
        Long userId= (Long) session.getAttribute("loginId");
        boolean exists= boardService.likeStatus(id,userId);
        session.setAttribute("existsByLike",exists);

        /* 댓글 목록 가져오기 */
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());

        return "detail";
    }

    @GetMapping("/board/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/board/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model,
                         @PageableDefault(page=1) Pageable pageable) throws IOException{
        String username= String.valueOf(session.getAttribute("loginUsername"));
        BoardDTO board = boardService.update(boardDTO,username);
        model.addAttribute("board", board);
        model.addAttribute("page",pageable.getPageNumber()); //페이지 번호도 같이 넘겨줘서 목록으로 돌아갈수 있게함
        //return "detail";
        return "redirect:/board/" + boardDTO.getId();
    }

    @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/paging";
    }

    // /board/paging?page=1
    @GetMapping("/board/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model,
                         String searchKeyword,String searchCategory) {

        /* 검색 기능 */
        Page<BoardDTO> boardList= null; //기본 페이징
        //searchKeyword -> 검색하려는 단어
        if(searchKeyword == null && searchCategory == null){ //검색하지 않을때
            boardList = boardService.paging(pageable);
        }else if(searchKeyword != null && searchCategory == null){ //검색만
            boardList = boardService.boardSearchList(searchKeyword,pageable);
        }else if(searchKeyword == null && searchCategory != null){//카테고리 검색만
            boardList = boardService.categorySearchList(searchCategory,pageable);
        }

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList); //페이징
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 현재 날짜 구하기 -> 최신글 표시를 위함.
        LocalDate now = LocalDate.now();
        // 포맷 정의
        DateTimeFormatter Time = DateTimeFormatter.ofPattern("yy-MM-dd");
        model.addAttribute("nowTime",now.format(Time));


        return "paging";

    }

}


