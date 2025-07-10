package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.utils.constant.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Role findByName(RoleName name);

    boolean existsByName(RoleName name);
}
