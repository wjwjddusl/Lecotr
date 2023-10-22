package com.mysite.bookstore.repository;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.BoardLike;
import com.mysite.bookstore.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> { //entity 클래스명과 pk의 기본자료형
    //update board_table set board_hits = board_hits+1 where id=? 글의 조회수 올리기.

    // update 나 delete 시행시 @Modifying 어노테이션 필수
    //엔티티 기준 쿼리문 *별칭 사용 필수.
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits = b.boardHits+1 where b.id=:id") //@Param 의 id와 매칭
    void updateHits(@Param("id") Long id);

    //검색 기능 (제목으로 검색)
    Page<BoardEntity> findByBoardTitleContaining(String searchKeyword,Pageable pageable);

    //카테고리 검색
    Page<BoardEntity> findByCategory(String searchCategory, Pageable pageable);

    Page<BoardEntity> findByUser(UserEntity user,Pageable pageable);
}
