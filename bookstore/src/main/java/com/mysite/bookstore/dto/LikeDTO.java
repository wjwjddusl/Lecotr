package com.mysite.bookstore.dto;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDTO {
    private BoardEntity board;
    private UserEntity user;
    private Long searchBoardId;
}
