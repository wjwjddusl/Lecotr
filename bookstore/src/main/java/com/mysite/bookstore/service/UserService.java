package com.mysite.bookstore.service;

import com.mysite.bookstore.dto.BoardDTO;
import com.mysite.bookstore.entity.*;
import com.mysite.bookstore.repository.BoardRepository;
import com.mysite.bookstore.repository.LikeRepository;
import com.mysite.bookstore.repository.UserRepository;
import com.mysite.bookstore.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;

    //repository 에서는 엔티티로 작업 ( dto 로 넘겨받았기 떄문에 변환 필요)
    public void save(UserDTO userDTO) {
        //1. dto 객체 -> entity 객체 변환
        userDTO.setRole(RoleType.USER); //권한부여
        UserEntity userEntity=UserEntity.toUserEntity(userDTO);
        //2. repository save 메서드 호출
        userRepository.save(userEntity);
    }

    public UserDTO login(UserDTO userDTO ) {
        //1.회원이 입력한 이메일을 db 에서 조회
        Optional<UserEntity> byUserEmail = userRepository.findByEmail(userDTO.getEmail());
        if(byUserEmail.isPresent()){
            //db 에 이메일이 존재하면 -> 회원
            //2.db 에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 같은지 대조
            UserEntity userEntity = byUserEmail.get(); //optional 로 감싸진 객체를 빼냄 (사용자가 입력한 패스워드)
            if(userEntity.getPassword().equals(userDTO.getPassword())){
                // 회원 정보 일치 -> 로그인 성공
                //entity -> dto 변환
                UserDTO dto = UserDTO.toUserDTO(userEntity);
                return dto;
            }else{ return null;}
        }else{
            //조회 결과 없음 -> 회원 X
            return null;
        }

    }

    //회원 리스트
    public List<UserDTO> findAll() {
        List<UserEntity> userEntityList = userRepository.findAll();
        //entity -> dto 로 변환
        List<UserDTO> userDTOList = new ArrayList<>();
        for (UserEntity userEntity : userEntityList){
            //for 문을 통해 엔티티 리스트를 하나씩 dto 리스트로 바꿈
            userDTOList.add(UserDTO.toUserDTO(userEntity));
        }
        return userDTOList;
    }

    //내정보 보기
    public UserDTO findByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId);
        UserDTO userDTO = UserDTO.toUserDTO(user);
        return userDTO;
    }

    public Page<BoardDTO> findByMyBoard(Long userId, Pageable pageable){
        UserEntity user = userRepository.findById(userId);
        //내가 작성한 글 찾기
        int page= pageable.getPageNumber()-1;
        int pageLimit = 10;
        Page<BoardEntity> byUser = boardRepository.findByUser(user,PageRequest.of(page, pageLimit,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<BoardDTO> boardDTOList = byUser.map(board -> new BoardDTO(board.getId(), board.getWriter(),
                board.getBoardTitle(), board.getCategory(), board.getBoardHits(), board.getCreatedTime()));


        return boardDTOList;

    }

    // 좋아요한 글 검색
    @Transactional
    public List<BoardDTO> findLikeBoard(Long userId){
        UserEntity user = userRepository.findById(userId); //현재 유저의 아이디 추출
        List<BoardLike> byUser = likeRepository.findByUser(user); //유저가 누른 좋아요 글 검색
        List<BoardEntity> likeBoard = new ArrayList<>();

        if(!byUser.isEmpty()){
            for(int i=0; i<byUser.size(); i++){
                BoardEntity board = byUser.get(i).getBoard(); //좋아요 리스트에서 boardId 추출
                likeBoard.add(board);
            }

            List<BoardEntity> likeBoardList = new ArrayList<>();
            for(int i=0; i<likeBoard.size(); i++){
                // boardId 로 board 정보 불러오기
                Optional<BoardEntity> byId = boardRepository.findById(likeBoard.get(i).getId());
                likeBoardList.add(byId.get());
            }

            List<BoardDTO> boardDTOList = new ArrayList<>();
            for(BoardEntity board: likeBoardList){
                boardDTOList.add(BoardDTO.toBoardDTO(board)); //Entity 리스트를 DTO 리스트로 변환(전달을 위함)
            }

            return boardDTOList;
        }else{
            List<BoardDTO> boardDTO = new ArrayList<>();
            return boardDTO; //빈거 전달
        }

    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    //이메일 중복 체크
    public String emailCheck(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()){
            //이메일이 존재하면 -> 중복
            return null;
        }else{
            //이메일이 존재하지 않으면 -> 중복X
            return "ok";
        }
    }

    public String userNameCheck(String username) {
        System.out.println("유저2:"+username);
        UserEntity byUsername = userRepository.findByUsername(username);

        if(byUsername != null){
            return null;
        }else{
            return "ok";
        }
    }

    public void Update(UserDTO userDTO) {
        UserEntity user = userRepository.findById(userDTO.getId());
        List<BoardLike> byUser = likeRepository.findByUser(user);

        userDTO.setRole(RoleType.USER); //권한부여
        UserEntity userEntity = UserEntity.toUpdateEntity(userDTO,byUser); //like 다시저장 (오류발생 때문)
        userRepository.save(userEntity); //다시 저장
    }

    public UserDTO userUpdate(Long userId) {
        UserEntity user = userRepository.findById(userId);
        return UserDTO.toUserDTO(user);
    }

    // 유저 업데이트

}
