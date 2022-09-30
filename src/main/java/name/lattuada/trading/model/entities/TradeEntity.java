package name.lattuada.trading.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "trades")
@Getter
@Setter
@ToString
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trd_id")
    private UUID id;

    @Column(name = "trd_ord_sell_id")
    private UUID orderSellId;

    @Column(name = "trd_ord_buy_id")
    private UUID orderBuyId;

    @Column(name = "trd_price")
    private Double price;

    @Column(name = "trd_quantity")
    private Long quantity;

}
