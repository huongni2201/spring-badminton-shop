package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Permission;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(pPermissions.getTotalPages());
        meta.setTotalItems(pPermissions.getTotalElements());

        return ResultPaginationDTO.builder()
                .meta(meta)
                .result(pPermissions.getContent())
                .build();
    }

    public Permission create(Permission permission) {
        if (isPermissionExist(permission)) {
            return permission;
        }

        return this.permissionRepository.save(permission);
    }

    public Permission update(Permission permission) {
        Permission permissionInDb = this.permissionRepository.findById(permission.getId()).orElse(null);

        if (permissionInDb != null) {
            permissionInDb.setModule(permission.getModule());
            permissionInDb.setApiPath(permission.getApiPath());
            permissionInDb.setMethod(permission.getMethod());
            permissionInDb.setName(permission.getName());
        }
        return this.permissionRepository.save(permissionInDb);
    }

    public void delete(Long id) {
        Permission permission = this.permissionRepository.findById(id).orElse(null);

        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));

        this.permissionRepository.delete(permission);
    }

    public Permission fetchById(Long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }
}
