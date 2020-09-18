package study.datajpa.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void createMemberTest(){
        try {
            Member.createMember("memberA", 10, null);
        }
        catch (IllegalStateException e){
            System.out.println( e.getMessage());
        }
    }

    @Test
    public void memberTest(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = Member.createMember("memberA", 10, teamA);
        Member memberB = Member.createMember("memberB", 10, teamA);
        Member memberC = Member.createMember("memberC", 10, teamB);
        Member memberD = Member.createMember("memberD", 10, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        members.stream().forEach(m -> {
            System.out.println("member " + m);
            System.out.println("team " + m.getTeam());
        });

    }
}