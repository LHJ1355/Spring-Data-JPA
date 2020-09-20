package study.datajpa.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

//콜백 리스너 클래스 지정
//JPA 엔티티 리스너 == 엔티티 persist, update 에 대한 감사(Auditing) 정보를 캡처
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity{
    //AuditorAware Bean 객체에서 사용자 정보를 가져옴 .getCurrentAuditor 메소드
    @Column(updatable = false)
    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
