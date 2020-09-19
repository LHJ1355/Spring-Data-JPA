package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired private EntityManager em;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamRepository teamRepository;

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

    @Test
    void findMemberDto(){
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member memberA = Member.createMember("memberA", 10, team);
        Member memberB = Member.createMember("memberB", 20, team);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        //then
        memberDtos.stream().forEach(m -> {
            System.out.println("memberDto -> " + m);
        });
    }

    @Test
    void returnType(){
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member memberA = Member.createMember("memberA", 10, team);
        Member memberA_1 = Member.createMember("memberA", 15, team);
        Member memberB = Member.createMember("memberB", 20, team);

        memberRepository.save(memberA);
        memberRepository.save(memberA_1);
        memberRepository.save(memberB);
        
        //when
        List<Member> memberList = memberRepository.findListByUsername("memberA");
        Member member = memberRepository.findMemberByUsername("memberB");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("memberB");

        //then
        memberList.stream().forEach(m -> System.out.println("memberList -> " + m));
        System.out.println("member -> " + member);
        System.out.println("optional Member -> " + optionalMember);
    }

    @Test
    void paging(){
        //given
        Team team = new Team("team");
        teamRepository.save(team);

        memberRepository.save(Member.createMember("memberA", 10, team));
        memberRepository.save(Member.createMember("memberA", 20, team));
        memberRepository.save(Member.createMember("memberA", 30, team));
        memberRepository.save(Member.createMember("memberA", 40, team));
        memberRepository.save(Member.createMember("memberA", 50, team));

        //then
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> pagedMember = memberRepository.findPageByUsername("memberA", pageRequest);
        Slice<Member> slicedMember = memberRepository.findSliceByUsername("memberA", pageRequest);

        //Page 의 content를 Dto로 맵핑
        Page<MemberDto> toMap = pagedMember.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));


        //then
        List<Member> content = pagedMember.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(pagedMember.getTotalElements()).isEqualTo(5);
        assertThat(pagedMember.getNumber()).isEqualTo(0);
        assertThat(pagedMember.getTotalPages()).isEqualTo(2);
        assertThat(pagedMember.isFirst()).isTrue();
        assertThat(pagedMember.hasNext()).isTrue();
    }

    @Test
    void bulkAgePlus(){
        //given
        Team team = new Team("team");
        teamRepository.save(team);

        memberRepository.save(Member.createMember("memberA", 10, team));
        memberRepository.save(Member.createMember("memberA", 20, team));
        memberRepository.save(Member.createMember("memberA", 30, team));
        memberRepository.save(Member.createMember("memberA", 40, team));
        memberRepository.save(Member.createMember("memberA", 50, team));

        //then
        //영속성 컨텍스트에 저장된 member들을 DB에 직접 쿼리를 날리는 JPQL이 변경할 수 있는 이유
        //JPQL을 실행하는 경우 영속성 컨텍스트의 쿼리들을 모아놓은 쓰기 지연 SQL 저장소의 쿼리들이 flush되어 DB에 반영되기 때문이다.
        int cnt = memberRepository.bulkAgePlus(30);

        //벌크 연산은 db에 직접 query 하기 때문에 영속성 컨텍스트 와 동일성이 보장 되지 않는다. db 값은 변경되도 영속성 컨텍스트는 변경 x
        //따라서 벌크연산 후에는 영속성 컨텍스트를 비워줘야 한다.
        //em.clear();   //em.clear() 대신 @Modifying 의 옵션을 clearAutomatically = true 로 두어도 된다.

        List<Member> members = memberRepository.findByUsername("memberA");
        members.stream().forEach(m -> System.out.println("member -> " + m));

        //then
        assertThat(cnt).isEqualTo(3);
    }

    @Test
    void EntityGraph(){
        Team team = new Team("team");
        teamRepository.save(team);

        memberRepository.save(Member.createMember("memberA", 10, team));
        memberRepository.save(Member.createMember("memberA", 20, team));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findEntityGraphByUsername("memberA");
        members.stream().forEach(m -> System.out.println(m.getTeam().getName()));

        memberRepository.findMembers();
    }
}