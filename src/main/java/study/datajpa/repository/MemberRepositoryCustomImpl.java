package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 사용자 정의 리포지토리 구현
 */
//스프링 데이터 JPA 나 @Query(JPQL)로 구현하기 어려운경우 직접 메소드를 구현해야 한다
//스프링 JDBC 템플릿, MyBatis, DB CONNECTION 직접 사용, Querydsl 사용 등, 메소드를 직접 구현해야 하는 경우
//사용자 정의 리포지토리를 구현해야 한다.
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{
    //스프링 데이터 JPA 가 상속받는 사용자 정의 인터페이스의 이름 + Impl 로 구현클래스의 이름을 정한다.
    //그래야만 스프링 데이터 JPA 가 사용자 정의 인터페이스의 메소드를 콜 할 경우, 구현체를 인식할 수 있다.
    //== 사용자 정의 리포지토리 구현의 유일한 규칙
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
