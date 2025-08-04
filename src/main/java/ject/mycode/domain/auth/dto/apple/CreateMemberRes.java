package ject.mycode.domain.auth.dto.apple;

public record CreateMemberRes(
        String memberNo
){
    public static CreateMemberRes from(String memberNo) {
        return new CreateMemberRes(memberNo);
    }
}
