package com.mysite.bookstore.dto;

import com.mysite.bookstore.entity.BoardEntity;
import com.mysite.bookstore.entity.RoleType;
import com.mysite.bookstore.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private RoleType role;
    private LocalDateTime createDate;
    private LocalDateTime UpdatedTime;

    public static UserDTO toUserDTO(UserEntity userEntity){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPassword(userEntity.getPassword());
        userDTO.setCreateDate(userEntity.getCreateDate());
        userDTO.setUpdatedTime(userEntity.getUpdateTime());
        userDTO.setRole(userEntity.getRole());

        return userDTO;
    }


}
