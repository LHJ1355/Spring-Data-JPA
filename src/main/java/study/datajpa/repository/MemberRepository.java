package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 메소드 이름으로 쿼리 생성,  파라미터 바인딩은 위치기반
     */
    List<Member> findByUsername(String username);
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    //List<Member> findTop3By();   //Limit, 조회한 것중 3개
    //Member find___By();   //___에는 메소드에대한 설명
    //List<Member> findDistinctBy();  //DISTINCT

    /**
     * @Query, 리포지토리 메소드에 쿼리 정의,    파라미터 바인딩은 이름기반
     */
    @Query("select m from Member m where m.username = :username and m.age > :age")
    Member findUser(@Param("username") String username, @Param("age") int age);


    /**
     *
     * @Query, 값, DTO 조회하기
     */
    @Query("select m.username from Member m")
    List<String> findMemberUsername();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name)from Member m join m.team t")
    List<MemberDto> findMemberDto();

}
