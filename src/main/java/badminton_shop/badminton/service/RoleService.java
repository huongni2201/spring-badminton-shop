package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.utils.constant.RoleName;
import badminton_shop.badminton.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(RoleName name) {
        return roleRepository.findByName(name);
    }

    public List<Role> fetchAllRoles() {
        return roleRepository.findAll();
    }
}
