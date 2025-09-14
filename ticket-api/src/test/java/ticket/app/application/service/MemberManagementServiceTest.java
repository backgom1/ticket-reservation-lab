package ticket.app.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ticket.app.domain.model.Member;
import ticket.app.infra.repository.MemberJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class MemberManagementServiceTest {

    @Autowired
    private MemberManagementService memberManagementService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;


    @Test
    @DisplayName("사용자 생성이 완료되는지 테스트한다.")
    void register() {
        Member member = Member.register("username1", "aA123456789!", "example02@example.com", new BCryptPasswordEncoder());
        memberJpaRepository.save(member);
        assertThat(memberJpaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 계정이 중복된지 확인한다.")
    void duplicateUsername() {
        Member member = Member.register("username1", "aA123456789!", "example02@example.com", new BCryptPasswordEncoder());
        memberJpaRepository.save(member);
        boolean username1 = memberManagementService.duplicateNicknameCheck("username1");
        assertThat(username1).isTrue();
    }
}