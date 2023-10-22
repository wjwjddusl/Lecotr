package com.mysite.bookstore.service;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardFileEntity;
import com.mysite.bookstore.entity.UserEntity;
import com.mysite.bookstore.repository.BoardFileRepository;
import com.mysite.bookstore.repository.BoardRepository;
import com.mysite.bookstore.dto.BoardDTO;
import com.mysite.bookstore.repository.LikeRepository;
import com.mysite.bookstore.repository.UserRepository;
import com.sun.net.httpserver.HttpsServer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//DTO -> Entity (Entity 클래스에서 진행)
//Entity -> DTO (DTO 클래스에서 진행)
// 컨트롤러에서 호출시에는 dto / 리포지터리에서 넘겨줄때는 entity

@Service
@RequiredArgsConstructor //리포지터리 생성자 주입
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    public void save(BoardDTO boardDTO,String username) throws IOException {
        //System.out.println("사용자"+username);
        boardDTO.setWriter(username); //writer에도 저장
        UserEntity user = userRepository.findByUsername(username); // username 검색
        boardDTO.setUser(user);
        //UserEntity 의 pk인 id를 BoardEntity 의 fk인 user_id에 자장하기위해 User 의 정보를 가져와 저장

        // 파일 첨부 여부에 따라 로직 분리
        if (boardDTO.getBoardFile().isEmpty()) {
            // 첨부 파일 없음.
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 첨부 파일 있음.
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦
                // 내사진.jpg => 839798375892_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
            MultipartFile boardFile = boardDTO.getBoardFile(); // 1.
            String originalFilename = boardFile.getOriginalFilename(); // 2.
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3.
            String savePath = "D:/JAVA/bookstore_img/"  + storedFileName; // 4. C:/springboot_img/9802398403948_내사진.jpg
//            String savePath = "/Users/사용자이름/springboot_img/" + storedFileName; // C:/springboot_img/9802398403948_내사진.jpg
            boardFile.transferTo(new File(savePath)); // 5.
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(savedId).get();

            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
            boardFileRepository.save(boardFileEntity);

        }

    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity: boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO,String username) throws IOException{
        //다시 저장
        boardDTO.setWriter(username);
        UserEntity user = userRepository.findByUsername(username);
        boardDTO.setUser(user);

        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);

        return findById(boardDTO.getId());
    }

    //파일 삭제 (수정 전에 삭제)
    @Transactional
    public void deleteFile(Long id) {
        Optional<BoardEntity> byId = boardRepository.findById(id);
        boardFileRepository.deleteByBoardEntity(byId.get()); //파일삭제
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수
        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        // page 위치에 있는 값은 0부터 시작
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부


        // 목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getWriter(), board.getBoardTitle(), board.getCategory(),board.getBoardHits(), board.getCreatedTime()));
        return boardDTOS;
    }

    public Page<BoardDTO> paging_index(Pageable pageable) {
        int page= pageable.getPageNumber() - 1; //전체 페이지 수, page 위치에 있는 값은 0부터 시작. (실제는 1부터)
        int pageLimit = 5; //한 페이지에 보여지는 게시글 개수
        //DB 로부터 페이징 처리된 글을 가져옴. (findAll)
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit,
                        Sort.by(Sort.Direction.DESC, "boardHits")));  //조회수 기준
        Page<BoardDTO> boardDTOS =
                boardEntities.map(board -> new BoardDTO(board.getId(), board.getWriter(),board.getBoardTitle(), board.getCategory(), board.getBoardHits(), board.getCreatedTime()));
        //Page 객체에서 제공하는 메서드, board(=객체)

        return boardDTOS;

    }

    //검색기능
    public Page<BoardDTO> boardSearchList(String searchKeyword,Pageable pageable){
        //@PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.DESC)
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10;
        Page<BoardEntity> byTitleContaining =
                boardRepository.findByBoardTitleContaining(searchKeyword
                        ,PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"))); //검색
        //dto 로 변환
        Page<BoardDTO> boardDTOS =
                byTitleContaining.map(board -> new BoardDTO(board.getId(), board.getWriter(),board.getBoardTitle(), board.getCategory(), board.getBoardHits(), board.getCreatedTime()));
        System.out.println("페이지 개수:"+ byTitleContaining.getTotalPages());
        System.out.println("dto 내용:"+ boardDTOS);
        return boardDTOS;
    }

    //카테고리 검색
    public Page<BoardDTO> categorySearchList(String category, Pageable pageable){
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10;
        Page<BoardEntity> searchCategory = boardRepository.findByCategory(category,
                PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        //dto 로 변환
        Page<BoardDTO> boardDTOS =
                searchCategory.map(board -> new BoardDTO(board.getId(), board.getWriter(),board.getBoardTitle(), board.getCategory(), board.getBoardHits(), board.getCreatedTime()));

        return boardDTOS;
    }

    //상세보기 클릭시 관심 버튼 상태
    public boolean likeStatus(Long id, Long userId) {
        //LikeRepository 에서 boardId 는 BoardEntity 형이기 때문에 Long 타입의 id 를 BoardEntity 로 바꿔서
        // BoardDTO 에 저장후 그것을 LikeRepository 에 전달 (BoardDTO 에 BoardEntity 타입의 boardId 별도로 생성함)
        Optional<BoardEntity> board= boardRepository.findById(id);
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardId(board.get()); //Optional 타입이기 때문에 get 을 사용해 Entity 타입으로 변환.

        UserEntity user = userRepository.findById(userId); //현재 세션에 있는 유저의 아이디를 받아서 검색.

        boolean exists = likeRepository.existsByBoardAndUser(boardDTO.getBoardId(),user);
        return exists;
    }


}

