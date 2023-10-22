package com.mysite.bookstore.entity;

import com.mysite.bookstore.dto.LikeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "like_table")
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //좋아요와 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    //좋아요와 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public BoardLike toLikeEntity(LikeDTO likeDTO){
        BoardLike boardLike = new BoardLike();
        boardLike.setUser(likeDTO.getUser());
        boardLike.setBoard(likeDTO.getBoard());

        return boardLike;
    }


}
