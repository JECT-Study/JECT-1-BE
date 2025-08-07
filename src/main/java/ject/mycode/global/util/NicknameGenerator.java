package ject.mycode.global.util;

import java.util.List;
import java.util.Random;

public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "귀여운", "조용한", "똑똑한", "배고픈", "졸린",
            "깜찍한", "우아한", "용감한", "활발한", "느긋한",
            "즐거운", "새로운", "상냥한", "다정한", "예쁜",
            "순한", "상큼한", "강한", "따뜻한", "멋진"
    );

    private static final List<String> NOUNS = List.of(
            "고양이", "강아지", "여우", "곰돌이", "햄스터",
            "기린", "토끼", "코끼리", "펭귄", "사자",
            "호랑이", "너구리", "다람쥐", "돼지", "고래",
            "참새", "올빼미", "수달", "개미", "청설모"
    );


    public static String generate() {
        String adjective = ADJECTIVES.get(new Random().nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(new Random().nextInt(NOUNS.size()));
        return adjective + " " + noun + new Random().nextInt(10);
    }
}
