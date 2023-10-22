package com.mysite.bookstore.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity { //시간정보

    @CreationTimestamp //생성시간
    @Column(updatable = false) //수정시에는 반응x
    private LocalDateTime createdTime;

    @UpdateTimestamp //수정시간
    @Column(insertable = false) //insert 시에는 반응x
    private  LocalDateTime updatedTime;

}
