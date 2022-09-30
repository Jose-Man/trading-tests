package name.lattuada.trading.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "securities")
@Getter
@Setter
@ToString
public class SecurityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sec_id")
    private UUID id;

    @Column(name = "sec_name")
    private String name;

}
