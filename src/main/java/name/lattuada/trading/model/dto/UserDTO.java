package name.lattuada.trading.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    @NotBlank
    private String username;

    @NotBlank
    @ToString.Exclude
    private String password;

}
