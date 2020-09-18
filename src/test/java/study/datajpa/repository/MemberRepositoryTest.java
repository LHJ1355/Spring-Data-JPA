package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired private MemberRepository memberRepository;

    @Test
    void findByUsername() {
        //given
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        List<Member> findMember = memberRepository.findByUsername("memberA");

        //then
        assertThat(findMember.get(0).getUsername()).isEqualTo("memberA");
        assertThat(findMember.get(0).getAge()).isEqualTo(10);
    }

   @Test
    void findByUsernameAndAgeGreaterThen() {
       //given
       Member memberA = new Member("memberA", 10);
       Member memberB = new Member("memberA", 20);
       memberRepository.save(memberA);
       memberRepository.save(memberB);

       //when
       List<Member> findMember = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 10);

       //then
       assertThat(findMember.get(0)).isEqualTo(memberB);
       assertThat(findMember.size()).isEqualTo(1);
    }

    @Test
    void findUser(){
        //given
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        Member findMember = memberRepository.findUser("memberA", 15);

        //then
        assertThat(findMember).isEqualTo(memberB);
    }
}