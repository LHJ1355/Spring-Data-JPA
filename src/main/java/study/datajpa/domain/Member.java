package study.datajpa.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @ToString(of = {"id", "username", "age"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age){
        this.username = username;
        this.age = age;
    }

    //생성 메소드
    public static Member createMember(String username, int age, Team team) throws IllegalStateException{
        Member member = new Member(username, age);
        if(team == null) throw new IllegalStateException("team is null");
        member.changeTeam(team);
        return member;
    }

    //연관관계 메소드
    public void changeTeam(Team team){
        this.team = team;
        team.addMember(this);
    }

    public void changeName(String newName) {
        this.username = newName;
    }
}
