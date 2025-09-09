package ticket.app.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import ticket.app.domain.AbstractEntity;

import java.time.LocalDateTime;

import static org.springframework.util.Assert.state;

@Entity
@Getter
@Table(name = "members")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member extends AbstractEntity {

    @Embedded
    private Nickname nickname;

    @Embedded
    private Password password;

    private String email;

    private MemberStatus status;

    @CreatedDate
    @DateTimeFormat(style = "YYYY-MM-DD hh:mm:ss")
    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;


    public static Member register(String nickname, String password, String email, PasswordEncoder encoder) {
        Member member = new Member();
        member.nickname = new Nickname(nickname);
        member.password = Password.of(password, nickname, encoder);
        member.email = email;
        member.status = MemberStatus.PENDING;
        return member;
    }

    public static Member ofClaims(Long id, String nickname, String email) {
        Member member = new Member();
        member.claimsId(id);
        member.nickname = new Nickname(nickname);
        member.email = email;
        member.status = MemberStatus.ACTIVE;
        return member;
    }

    public void activate() {
        state(status == MemberStatus.PENDING, "Member is not pending");
        this.status = MemberStatus.ACTIVE;
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "Member is not ACTIVE");
        this.status = MemberStatus.DEACTIVATED;
    }

    public void changePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password = password.changePassword(rawPassword, passwordEncoder);
    }

    public boolean verifyPassword(String rawPassword, PasswordEncoder encoder) {
        return password.matches(rawPassword, encoder);
    }


}
