package name.lattuada.trading.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import name.lattuada.trading.model.EOrderType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ord_id")
    private UUID id;

    @Column(name = "ord_usr_id")
    private UUID userId;

    @Column(name = "ord_sec_id")
    private UUID securityId;

    @Column(name = "ord_type")
    @Enumerated(EnumType.STRING)
    private EOrderType type;

    @Column(name = "ord_price")
    private Double price;

    @Column(name = "ord_quantity")
    private Long quantity;

    @Column(name = "ord_fulfilled")
    private Boolean fulfilled = Boolean.FALSE;

}
