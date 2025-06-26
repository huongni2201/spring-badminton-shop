package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.utils.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
