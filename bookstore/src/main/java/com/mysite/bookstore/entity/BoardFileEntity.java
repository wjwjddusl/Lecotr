package com.mysite.bookstore.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="board_file_table")
public class BoardFileEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    // 파일과 게시글 -> N:1, 자식 정의 형태
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id") // 실제 DB 에 만들어지는 컬럼 이름
    private BoardEntity boardEntity; //부모 엔티티 타입으로 적어줘야함. 파일이 저장된 게시글

    //BoardFileEntity 로 변환하기
    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storedFileName){
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(boardEntity); //pk 가 아닌 부모 엔티티 객체를 넘겨줘야함

        return boardFileEntity;
    }

}
