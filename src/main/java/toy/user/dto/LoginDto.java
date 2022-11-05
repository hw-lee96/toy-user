package toy.user.dto;

import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter

// builder 패턴을 사용할 수 있게 함
@Builder

// 모든 인수를 받는 생성자를 만듦
@AllArgsConstructor

// 인수가 없는 생성자를 만듦
@NoArgsConstructor

public class LoginDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}