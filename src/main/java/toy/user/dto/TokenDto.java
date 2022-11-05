package toy.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Token 정보를 response 할 때 사용
public class TokenDto {

    private String token;
}