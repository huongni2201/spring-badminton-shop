package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Permission;
import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.repository.PermissionRepository;
import badminton_shop.badminton.utils.constant.RoleName;
import badminton_shop.badminton.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role findByName(RoleName name) {
        return roleRepository.findByName(name);
    }

    public boolean existsByName(RoleName name) {
        return roleRepository.existsByName(name);
    }

    public Role createRole(Role role) {

        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findAllById(reqPermissions);

            role.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Role dbRole = this.roleRepository.findById(role.getId()).orElse(null);

        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findAllById(reqPermissions);

            role.setPermissions(dbPermissions);
        }

        if (dbRole != null) {
            dbRole.setName(role.getName());
            dbRole.setDescription(role.getDescription());
            dbRole.setPermissions(role.getPermissions());
            dbRole.setPermissions(role.getPermissions());
            return this.roleRepository.save(dbRole);
        }
        return null;
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(pRole.getTotalPages());
        meta.setTotalItems(pRole.getTotalElements());

        return ResultPaginationDTO.builder()
                .meta(meta)
                .result(pRole.getContent())
                .build();
    }

    public void deleteRoleById(Long id) {
        this.roleRepository.deleteById(id);
    }
}
