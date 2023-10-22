package com.mysite.bookstore.controller;

import com.mysite.bookstore.Exception.MyException;
import com.mysite.bookstore.dto.BoardDTO;
import com.mysite.bookstore.dto.UserDTO;
import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardLike;
import com.mysite.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {

    //생성자 주입(자동)
    private final UserService userService;

    @GetMapping("/user/save")
    public String register(){
        return "register";
    }

    @PostMapping("/user/save") //form 태그를 통해 입력받은 값 가져오기
    public String save(@ModelAttribute UserDTO userDTO){
        userService.save(userDTO); //저장을 위해 넘겨줌
        return "redirect:/";
    }

    @GetMapping("/user/login")
    public String loginForm(){return "login";}

    @PostMapping("/user/login")
    public String login(@ModelAttribute UserDTO userDTO, HttpSession session){
        UserDTO loginResult = userService.login(userDTO);
        if(loginResult != null){
            //로그인이 되었다면
            session.setAttribute("loginEmail", loginResult.getEmail()); //로그인한 회원의 이메일 정보를 세션에 담음
            session.setAttribute("loginUsername",loginResult.getUsername());
            session.setAttribute("RoleType",loginResult.getRole()); //admin 유저일때
            session.setAttribute("loginId",loginResult.getId());
            return "redirect:/";
        }else{
            //로그인이 안되었다면
            return "login";
        }

    }
    //회원 검색

    //회원관리(Admin)
    @GetMapping("/user/manage")
    public String manage(Model model){
        List<UserDTO> userDTOList=userService.findAll(); //회원 목록 가져오기
        model.addAttribute("userList",userDTOList); //model 에 회원 리스트 담기
        return "manage";
    }

    //회원 삭제
    @GetMapping("/user/delete/{id}")
    public String deleteById(@PathVariable Long id){
        userService.deleteById(id);
        return "redirect:/user/manage";
    }


    //로그아웃
    @GetMapping("/user/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/user/myPage")
    public String myPage(HttpSession session, Model model,@PageableDefault(page=1) Pageable pageable){
        Long userId= (Long) session.getAttribute("loginId");
        // 내정보 불러오기
        UserDTO userDTO = userService.findByUserId(userId);
        model.addAttribute("userInfo",userDTO);

        //내가 쓴글 불러오기
        Page<BoardDTO> byMyBoard = userService.findByMyBoard(userId,pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < byMyBoard.getTotalPages()) ? startPage + blockLimit - 1 : byMyBoard.getTotalPages();

        model.addAttribute("total",byMyBoard.getTotalPages());
        model.addAttribute("myBoard",byMyBoard);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 좋아요한 글 불러오기
        List<BoardDTO> likeBoard = userService.findLikeBoard(userId);
        model.addAttribute("likeBoard",likeBoard);
        model.addAttribute("likeTotal",likeBoard.size());

        return "myPage";
    }

    @PostMapping("/user/email-check")
    public @ResponseBody String checkEmail(@RequestParam("join-email") String email) throws MyException {
        //ajax 사용시 RequestBody 어노테이션 필수
        String checkResult = userService.emailCheck(email);
        return checkResult;
    }

    @PostMapping("/user/username-check")
    public @ResponseBody String checkUsername(@RequestParam("username") String username) throws MyException {
        //ajax 사용시 RequestBody 어노테이션 필수
        System.out.println("유저:"+username);
        String checkResult = userService.userNameCheck(username);
        return checkResult;
    }

    //이메일 수정
    @GetMapping("/user/email-update")
    public String emailUpdateForm(HttpSession session,Model model){ //정보 가져오기
        Long userId= (Long) session.getAttribute("loginId");
        UserDTO userDTO = userService.userUpdate(userId);
        model.addAttribute("emailUpdate",userDTO);
        return "updateEmail";
    }

    //비밀번호 수정
    @GetMapping("/user/password-update")
    public String pwdUpdateForm(HttpSession session,Model model){ //정보 가져오기
        Long userId= (Long) session.getAttribute("loginId");
        UserDTO userDTO = userService.userUpdate(userId);
        model.addAttribute("pwdUpdate",userDTO);
        return "updatePwd";
    }

    //공통
    @PostMapping("/user/update")
    public String Update(@ModelAttribute UserDTO userDTO){
        userService.Update(userDTO);
        return "redirect:/user/myPage";
    }

    //비밀번호를 수정한 경우 로그아웃후 로그인 창으로 이동.
    @PostMapping("/user/pwdUpdate")
    public String pwdUpdate(@ModelAttribute UserDTO userDTO,HttpSession session){
        userService.Update(userDTO);
        session.invalidate(); //로그아웃
        return "redirect:/user/login";
    }

}
