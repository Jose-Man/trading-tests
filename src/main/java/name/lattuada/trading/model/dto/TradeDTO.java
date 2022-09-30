package name.lattuada.trading.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Getter
@Setter
@ToString
public class TradeDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    @name.lattuada.trading.validator.UUID
    private UUID orderSellId;

    @name.lattuada.trading.validator.UUID
    private UUID orderBuyId;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Long quantity;

}
