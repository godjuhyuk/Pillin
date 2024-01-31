package com.ssafy.yourpilling.rank.model.service.impl;

import com.ssafy.yourpilling.common.*;
import com.ssafy.yourpilling.pill.model.dao.entity.*;
import com.ssafy.yourpilling.pill.model.dao.jpa.NutritionJpaRepository;
import com.ssafy.yourpilling.pill.model.dao.jpa.OwnPillJpaRepository;
import com.ssafy.yourpilling.pill.model.dao.jpa.PillJpaRepository;
import com.ssafy.yourpilling.pill.model.dao.jpa.PillMemberJpaRepository;
import com.ssafy.yourpilling.rank.model.dao.entity.Rank;
import com.ssafy.yourpilling.rank.model.dao.entity.RankMidCategory;
import com.ssafy.yourpilling.rank.model.dao.jpa.RankMidCategoryRepository;
import com.ssafy.yourpilling.rank.model.dao.jpa.RankRepository;
import com.ssafy.yourpilling.rank.model.service.RankService;
import com.ssafy.yourpilling.security.auth.model.dao.entity.Member;
import com.ssafy.yourpilling.security.auth.model.dao.jpa.MemberRepository;
import jakarta.persistence.EntityManager;
import org.aspectj.weaver.ast.Literal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
class RankServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PillMemberJpaRepository pillMemberJpaRepository;

    @Autowired
    private PillJpaRepository pillJpaRepository;

    @Autowired
    private OwnPillJpaRepository ownPillJpaRepository;

    @Autowired
    private NutritionJpaRepository nutritionJpaRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RankService rankService;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private RankMidCategoryRepository rankMidCategoryRepository;

    @Test
    @DisplayName("랭크 집계 및 생성")
    public void generateRank() {
        midCategories();

        List<Pill> pills = pills();
        pillJpaRepository.saveAll(pills);

        List<Member> members = members();

        ownPills(members, pills);

        rankService.generateWeeklyRank();

        List<Rank> all = rankRepository.findAll();

        System.out.println("-------------");
        System.out.println(all);
    }

    private void midCategories() {
        List<RankMidCategory> categories = new ArrayList<>();

        // 성별 및 나잇대 카테고리
        for (Gender g : Gender.values()) {
            for (AgeGroup a : AgeGroup.values()) {
                categories.add(RankMidCategory.builder().categoryNm(a.getRange()+","+g.getGender()).build());
            }
        }

        // 영양소 카테고리
        for (Nutrient n : Nutrient.values()) {
            categories.add(RankMidCategory.builder().categoryNm(n.getEnglish()).build());
        }

        // 건강고민 카테고리
        for (HealthConcern h : HealthConcern.values()) {
            categories.add(RankMidCategory.builder().categoryNm(h.getEnglish()).build());
        }

        rankMidCategoryRepository.saveAll(categories);
    }

    private void ownPills(List<Member> members, List<Pill> pills){
        List<OwnPill> all = List.of(
                OwnPill.builder().takeYN(true).build(),
                OwnPill.builder().takeYN(false).build(),
                OwnPill.builder().takeYN(true).build(),
                OwnPill.builder().takeYN(true).build(),
                OwnPill.builder().takeYN(false).build(),
                OwnPill.builder().takeYN(false).build(),
                OwnPill.builder().takeYN(true).build()
        );

        all.get(0).setMember(pillMemberJpaRepository.findByUsername(members.get(2).getUsername()).get());
        all.get(0).setPill(pills.get(0));

        all.get(1).setMember(pillMemberJpaRepository.findByUsername(members.get(2).getUsername()).get());
        all.get(1).setPill(pills.get(1));

        all.get(2).setMember(pillMemberJpaRepository.findByUsername(members.get(2).getUsername()).get());
        all.get(2).setPill(pills.get(2));

        all.get(3).setMember(pillMemberJpaRepository.findByUsername(members.get(3).getUsername()).get());
        all.get(3).setPill(pills.get(4));

        all.get(4).setMember(pillMemberJpaRepository.findByUsername(members.get(3).getUsername()).get());
        all.get(4).setPill(pills.get(1));

        all.get(5).setMember(pillMemberJpaRepository.findByUsername(members.get(4).getUsername()).get());
        all.get(5).setPill(pills.get(1));

        all.get(6).setMember(pillMemberJpaRepository.findByUsername(members.get(4).getUsername()).get());
        all.get(6).setPill(pills.get(3));

        ownPillJpaRepository.saveAll(all);
    }

    private List<Member> members() {
        return List.of(
                registerMember("t1", LocalDate.now().minusYears(6), Gender.WOMAN),
                registerMember("t2", LocalDate.now().minusYears(10), Gender.MAN),
                registerMember("t3", LocalDate.now().minusYears(20), Gender.MAN),
                registerMember("t4", LocalDate.now().minusYears(25), Gender.MAN),
                registerMember("t5", LocalDate.now().minusYears(27), Gender.WOMAN),
                registerMember("t6", LocalDate.now().minusYears(30), Gender.WOMAN),
                registerMember("t7", LocalDate.now().minusYears(39), Gender.MAN),
                registerMember("t8", LocalDate.now().minusYears(40), Gender.MAN)
        );
    }

    private List<Pill> pills() {
        return List.of(
                registerPill(0, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.VITAMINA.getEnglish()),
                                registerNutrition(Nutrient.VITAMINB2.getEnglish())
                        })),
                registerPill(1, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.CALCIUM.getEnglish()),
                                registerNutrition(Nutrient.VITAMINB12.getEnglish())
                        })),
                registerPill(2, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.CALCIUM.getEnglish()),
                                registerNutrition(Nutrient.VITAMINB2.getEnglish())
                        })),
                registerPill(3, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.PANTOTHENIC_ACID.getEnglish()),
                                registerNutrition(Nutrient.FOLIC_ACID.getEnglish())
                        })),
                registerPill(4, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.VITAMINA.getEnglish()),
                                registerNutrition(Nutrient.VITAMINB2.getEnglish())
                        })),
                registerPill(5, List.of(new Nutrition[]{
                                registerNutrition(Nutrient.FOLIC_ACID.getEnglish()),
                                registerNutrition(Nutrient.VITAMINA.getEnglish())
                        }))
        );
    }

    private Member registerMember(String username, LocalDate birthday, Gender gender) {
        Member member = Member
                .builder()
                .username(username)
                .password(encoder.encode("1234"))
                .name("ksb")
                .nickname("nick")
                .birth(birthday)
                .gender(gender)
                .role(Role.MEMBER)
                .build();

        if (memberRepository.findByUsername(username).isEmpty()) {
            memberRepository.save(member);
        }
        return member;
    }

    private Pill registerPill(int index, List<Nutrition> nutritions) {
        return Pill
                .builder()
                .name("pillName" + index)
                .manufacturer("제조사" + index)
                .usageInstructions("사용법" + index)
                .primaryFunctionality("주된기능성" + index)
                .precautions("주의사항" + index)
                .storageInstructions("보관 방법")
                .standardSpecification("기준 규격")
                .productForm(PillProductForm.TABLET)
                .imageUrl("이미지")
                .takeCycle(1)
                .takeCount(1)
                .takeOnceAmount(1)
                .createdAt(LocalDateTime.now())
                .nutritions(nutritions)
                .build();
    }

    private Nutrition registerNutrition(String name) {
        Nutrition nutrition = Nutrition
                .builder()
                .nutrition(name)
                .amount(1.5)
                .unit("mg")
                .createdAt(LocalDateTime.now())
                .includePercent("12")
                .build();

        if (nutritionJpaRepository.findByNutrition(name).isEmpty()) {
            nutritionJpaRepository.save(nutrition);
        }
        return nutrition;
    }
}