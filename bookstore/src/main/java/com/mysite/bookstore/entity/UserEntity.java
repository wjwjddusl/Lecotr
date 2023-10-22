package com.mysite.bookstore.entity;

import com.mysite.bookstore.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_table")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //1부터 시작, 자동 1씩 증가하도록 증가 전략 설정
    private  Long id; //회원 번호

    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 100)
    private String password; //비밀번호

    @Column(nullable = false, length = 100, unique = true)
    private String email; //아이디

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createDate; //가입 시간(현재 시간이 디폴트)

    @Column(insertable = false)
    @UpdateTimestamp
    private LocalDateTime updateTime;

    //회원가 게시글의 관계 ,user 가 관계의 주인
    @OneToMany(mappedBy = "user", fetch=FetchType.EAGER)
    private List<BoardEntity> boardEntityList= new ArrayList<>();

    //회원과 좋아요
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardLike> likes;


    //entity 로 변환하는 메서드
    public static UserEntity toUserEntity(UserDTO userDTO){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setId(userDTO.getId());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setRole(userDTO.getRole());
        userEntity.setCreateDate(userDTO.getCreateDate());

        return userEntity;
    }

    public static UserEntity toUpdateEntity(UserDTO userDTO,List<BoardLike> likes){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setId(userDTO.getId());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setRole(userDTO.getRole());
        userEntity.setLikes(likes);
        userEntity.setCreateDate(userDTO.getCreateDate());

        return userEntity;
    }

}
