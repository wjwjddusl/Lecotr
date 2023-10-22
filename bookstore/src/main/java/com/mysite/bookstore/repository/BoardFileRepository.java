package com.mysite.bookstore.repository;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFileEntity,Long> {

    void deleteByBoardEntity(BoardEntity boardId);
}
