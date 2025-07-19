package badminton_shop.badminton.controller;


import badminton_shop.badminton.domain.Permission;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.service.PermissionService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService pService;

    public PermissionController(PermissionService pService) {
        this.pService = pService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.pService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.pService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.pService.fetchById(permission.getId()) == null) {
            throw new IdInvalidException("Permission không tồn tại.");
        }

        if (this.pService.isPermissionExist(permission) && this.pService.isSameName(permission)) {
            throw new IdInvalidException("Permission đã tồn tại.");
        }

        return ResponseEntity.ok(this.pService.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.pService.fetchById(id) == null) {
            throw new IdInvalidException("Permission không tồn tại.");
        }

        this.pService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.pService.getPermissions(spec, pageable));
    }
}
