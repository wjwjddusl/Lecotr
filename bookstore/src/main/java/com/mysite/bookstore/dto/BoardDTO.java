package com.mysite.bookstore.dto;
import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.UserEntity;
import lombok.*;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString //필드값 확인시 사용
@NoArgsConstructor //기본생성자 자동 생성
@AllArgsConstructor //모든 필드를 매개변수로 하는 생성자 자동 생성
//DTO = Data Transfer Object 데이터 전송에 쓰이는 객체 (VO, Bean 등)
public class BoardDTO {
    private Long id;
    private BoardEntity boardId; //관심버튼 boardId.
    private String writer; //작성자
    private UserEntity user;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private String category; //카테고리
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private MultipartFile boardFile; // save.html -> Controller 파일 담는 용도
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    public BoardDTO(Long id, String writer, String boardTitle, String category,int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.writer = writer;
        this.boardTitle = boardTitle;
        this.category = category;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setWriter(boardEntity.getWriter());
        boardDTO.setUser(boardEntity.getUser());
        boardDTO.setCategory(boardEntity.getCategory()); //카테고리 추가
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
        if (boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 0
        } else {
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 1
            // 파일 이름을 가져가야 함.
            // orginalFileName, storedFileName : board_file_table(BoardFileEntity)
            // join
            // select * from board_table b, board_file_table bf where b.id=bf.board_id
            // and where b.id=?
            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
        }

        return boardDTO;
    }
}
