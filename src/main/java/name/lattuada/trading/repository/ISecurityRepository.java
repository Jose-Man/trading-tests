package name.lattuada.trading.repository;

import name.lattuada.trading.model.entities.SecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISecurityRepository extends JpaRepository<SecurityEntity, UUID> {
}
