package ticket.app.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {

    @Test
    @DisplayName("동일한 문자를 3회 이상 연속 실패 테스트")
    void hasThreeOrMoreRepeatingChars() {
        String nickname = "aaa";
        assertThatThrownBy(() -> new Nickname(nickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 문자를 3회 이상 연속으로 사용할 수 없습니다.");
    }


    @Test
    @DisplayName("연속된 문자/숫자를 4개 이상에 대한 실패 테스트")
    void hasFourOrMoreSequentialChars() {
        String nickname = "abcd";
        assertThatThrownBy(() -> new Nickname(nickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연속된 문자/숫자를 4개 이상 사용할 수 없습니다.");
    }

}