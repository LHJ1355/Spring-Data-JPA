package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    /**
     * 메소드 이름으로 쿼리 생성,  파라미터 바인딩은 위치기반
     */
    List<Member> findByUsername(String username);
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    //List<Member> findTop3By();   //Limit, 조회한 것중 3개
    //List<Member> find___By();   //___에는 메소드에대한 설명
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

    /**
     * 반환 타입
     */
    List<Member> findListByUsername(String username);   //컬렉션
    Member findMemberByUsername(String username);   //단건
    Optional<Member> findOptionalByUsername(String username);   //단건 Optional

    /**
     * 페이징과 정렬
     */
    //Page는 totalCount 필요, 자동으로 count 쿼리가 발생
    //Page의 필드값 totalPage, totalElement 는 totalCount 값이 있어야 계산 가능
    Page<Member> findPageByUsername(String username, Pageable pageable);

    //Slice는 totalCount 불필요, count 쿼리 발생 x
    //Slice는 다음페이지의 유,무를 요청하는 쿼리의 size(limit) 를 하나 더 키워서 가져옴으로써 판단함
    Slice<Member> findSliceByUsername(String username, Pageable pageable);

    //count query 는 join을 하지 않도록 최적화 할 수 있다.
    @Query(value = "select m from Member m join m.team", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 수정 쿼리
     */
    @Modifying(clearAutomatically = true)  //조회쿼리가 아닌 INSERT, UPDATE, DELETE 등, 변경, 삭제 쿼리메소드를 실행할 때 필요
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);     //update return 값 으로는, update 된 row 수가 나옴

    /**
     * @EntityGraph
     */
    @Override
    @EntityGraph(attributePaths = {"team"})     //jpa 의 fetch join 기능
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);      //메소드 이름 규칙도 fetch join 가능

    @EntityGraph(attributePaths = {"team"})        //@Query도 EntityGraph 사용 가능
    @Query("select m from Member m")
    List<Member> findMembers();

    /**
     * QueryHint, Lock
     */
    //QueryHint는 jpa 구현체에게 제공되는 힌트, 여기서는 구현체인 하이버네이트의 readOnly 값을 true로 넘겨 최적화 실행
    //하이버네이트 readOnly 는 스냅샷을 저장하지 않아 메모리를 적게 쓰고, 스냅샷이 없으므로 더티체킹 과정을 생략
    //transactional readOnly 는 flush 를 생략해 더티체킹 과정을 생략
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //DB의 select for update 처럼 select한 필드들로의 접근을 제한하는 기능을 jpa 에서 쉽게 제공해주는 annotation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);
}
