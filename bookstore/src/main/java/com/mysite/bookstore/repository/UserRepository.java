package com.mysite.bookstore.repository;

import com.mysite.bookstore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    //이메일로 회원정보를 조회하는 메서드 생성
    //select * from user_table where email=?
    Optional<UserEntity> findByEmail(String email);

    //user get
    UserEntity findByUsername(String username);

    UserEntity findById(Long id);

    void deleteById(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
