package ticket.app.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.app.domain.model.Member;
import ticket.app.domain.model.Nickname;
import ticket.app.infra.repository.MemberJpaRepository;
import ticket.app.presentation.dto.RegisterRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberManagementService {

    private final MemberJpaRepository memberJpaRepository;

    @Transactional
    public void register(RegisterRequest request) {
        Member account = Member.register(request.getNickname(), request.getPassword(), request.getEmail(), new BCryptPasswordEncoder());
        memberJpaRepository.save(account);
    }

    @Transactional(readOnly = true)
    public boolean duplicateNicknameCheck(String nickname) {
        return memberJpaRepository.existsByNickname(new Nickname(nickname));
    }
}
