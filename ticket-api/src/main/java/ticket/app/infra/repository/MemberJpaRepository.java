package ticket.app.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.app.domain.model.Member;
import ticket.app.domain.model.Nickname;
import ticket.app.domain.model.Password;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPasswordAndNickname(Password password, Nickname nickname);
    Optional<Member> findByIdAndNickname(Long id, Nickname nickname);
    boolean existsByNickname(Nickname nickname);
}
