package ject.mycode.global.util;

import java.util.List;
import java.util.Random;

public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "활발한", "느긋한", "귀여운", "상큼한", "배고픈", "졸린", "행복한", "똑똑한", "운동하는", "춤추는", "노래하는"
    );

    private static final List<String> NOUNS = List.of(
            "고양이", "강아지", "기린", "코끼리", "토끼", "펭귄", "호랑이", "햄스터", "개미", "너구리", "곰돌이", "돼지", "여우"
    );

    public static String generate() {
        String adjective = ADJECTIVES.get(new Random().nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(new Random().nextInt(NOUNS.size()));
        return adjective + " " + noun + new Random().nextInt(1000);
    }
}
