package study.querydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자를 만들어줌
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // 주인을 자바객체로 적는다
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
