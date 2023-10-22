package com.mysite.bookstore.repository;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardLike;
import com.mysite.bookstore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<BoardLike,Long> {

    Optional<BoardLike> findBoardLikeByUserAndBoard(UserEntity user, BoardEntity board);

    void deleteByBoardAndUser(BoardEntity board, UserEntity user);

    boolean existsByBoardAndUser(BoardEntity board, UserEntity user);

    List<BoardLike> findByUser(UserEntity user);
}
