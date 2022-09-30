package name.lattuada.trading.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import name.lattuada.trading.model.EOrderType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    @name.lattuada.trading.validator.UUID
    private UUID userId;

    @name.lattuada.trading.validator.UUID
    private UUID securityId;

    @NotNull
    private EOrderType type;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Long quantity;

    private Boolean fulfilled = Boolean.FALSE;

}
