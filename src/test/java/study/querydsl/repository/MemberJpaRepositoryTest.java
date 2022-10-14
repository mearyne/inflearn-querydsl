package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findByIdMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(member).isEqualTo(findByIdMember);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUserName("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void basicTest_querydsl() {
        Member member = new Member("member2", 11);
        memberJpaRepository.save(member);

        List<Member> result1 = memberJpaRepository.findAll_querydsl();
        assertThat(result1).containsExactly(member);

        Member result2 = memberJpaRepository.findById_querydsl(member.getId()).get();
        assertThat(result2).isEqualTo(member);

        List<Member> result3 = memberJpaRepository.findByUserName_querydsl("member2");
        assertThat(result3).containsExactly(member);

    }

    @Test
    public void searchTest() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition memberSearchCondition1 = new MemberSearchCondition();
        MemberSearchCondition memberSearchCondition2 = new MemberSearchCondition();
        memberSearchCondition2.setTeamName("teamA");

        MemberSearchCondition memberSearchCondition3 = new MemberSearchCondition();
        memberSearchCondition3.setTeamName("teamA");
        memberSearchCondition3.setUsername("member2");

        MemberSearchCondition memberSearchCondition4 = new MemberSearchCondition();
        memberSearchCondition4.setTeamName("teamA");
        memberSearchCondition4.setUsername("member3");

        MemberSearchCondition memberSearchCondition5 = new MemberSearchCondition();
        memberSearchCondition5.setAgeGoe(35);
        memberSearchCondition5.setAgeLoe(45);
        memberSearchCondition5.setTeamName("teamB");

        // when
        List<MemberTeamDto> result1 = memberJpaRepository.search(memberSearchCondition1);
        List<MemberTeamDto> result2 = memberJpaRepository.search(memberSearchCondition2);
        List<MemberTeamDto> result3 = memberJpaRepository.search(memberSearchCondition3);
        List<MemberTeamDto> result4 = memberJpaRepository.search(memberSearchCondition4);
        List<MemberTeamDto> result5 = memberJpaRepository.search(memberSearchCondition5);

        // then
        assertAll(
                () -> assertThat(result1.size()).isEqualTo(4)
                , () -> assertThat(result2.size()).isEqualTo(2)
                , () -> assertThat(result3.get(0).getMemberId()).isEqualTo(member2.getId())
                , () -> assertThat(result4.size()).isEqualTo(0)
                , () -> assertThat(result5.size()).isEqualTo(1)
                , () -> assertThat(result5).extracting("username").containsExactly("member4")
        );

    }
    
    @Test
    public void betweenAge() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
                
        // when
        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeLoe(25);
        memberSearchCondition.setAgeGoe(10);
        List<MemberTeamDto> result = memberJpaRepository.memberBetweenAge(memberSearchCondition);


        // then
        assertThat(result).extracting("age").containsExactlyInAnyOrder(10, 20);
        
    }
}