package toy.user.entity;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "authority")
@AllArgsConstructor
@NoArgsConstructor
public class Authority {
    @Id
    @Column(name="authority_name", length=50)
    @NotBlank
    private String authorityName;
}
