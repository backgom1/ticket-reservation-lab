package ticket.app.domain.model;

import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Nickname(String value) {
    public Nickname {

        if (hasThreeOrMoreRepeatingChars(value)) {
            throw new IllegalArgumentException("동일한 문자를 3회 이상 연속으로 사용할 수 없습니다.");
        }

        if (hasFourOrMoreSequentialChars(value)) {
            throw new IllegalArgumentException("연속된 문자/숫자를 4개 이상 사용할 수 없습니다.");
        }

    }

    /**
     * 동일한 문자가 3번 이상 반복되는지 확인합니다. (e.g., "aaa", "111")
     */
    private static boolean hasThreeOrMoreRepeatingChars(String nickname) {
        return Pattern.compile("(.)\\1{2,}").matcher(nickname).find();
    }


    /**
     * 연속된 문자/숫자가 4개 이상 사용되었는지 확인합니다. (e.g., "1234", "abcd", "qwer")
     */
    private static boolean hasFourOrMoreSequentialChars(String nickname) {
        String lowerCaseNickname = nickname.toLowerCase();

        for (int i = 0; i < lowerCaseNickname.length() - 3; i++) {
            char c1 = lowerCaseNickname.charAt(i);
            char c2 = lowerCaseNickname.charAt(i + 1);
            char c3 = lowerCaseNickname.charAt(i + 2);
            char c4 = lowerCaseNickname.charAt(i + 3);

            if ((c2 - c1 == 1 && c3 - c2 == 1 && c4 - c3 == 1) || (c1 - c2 == 1 && c2 - c3 == 1 && c3 - c4 == 1)) {
                return true;
            }
        }


        String[] keyboardRows = {"qwertyuiop", "asdfghjkl", "zxcvbnm"};
        for (String row : keyboardRows) {
            for (int i = 0; i < row.length() - 3; i++) {
                String sequence = row.substring(i, i + 4);
                if (lowerCaseNickname.contains(sequence) || lowerCaseNickname.contains(new StringBuilder(sequence).reverse().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

}
