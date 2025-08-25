package ticket.app.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ticket.app.domain.AbstractEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "members")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

    private String email;

    private String password;

    private String nickname;

    private MemberStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;
}
