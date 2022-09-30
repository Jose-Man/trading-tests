package name.lattuada.trading.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "usr_id")
    private UUID id;

    @Column(name = "usr_username")
    private String username;

    @Column(name = "usr_password")
    @ToString.Exclude
    private String password;

}
