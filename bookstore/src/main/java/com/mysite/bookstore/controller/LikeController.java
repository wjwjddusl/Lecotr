package com.mysite.bookstore.controller;

import com.mysite.bookstore.dto.LikeDTO;
import com.mysite.bookstore.service.LikeService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final HttpSession session;

    @GetMapping("/api/likes/{id}")
    public String addLikes(@PathVariable Long id, LikeDTO likeDTO) throws Exception{ //게시글 아이디
        String username= String.valueOf(session.getAttribute("loginUsername")); //현재 접속중 유저네임
        likeService.addLikes(id, username, likeDTO);
        return "redirect:/board/{id}";
    }


}
