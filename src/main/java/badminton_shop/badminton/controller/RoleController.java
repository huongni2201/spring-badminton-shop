package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.service.RoleService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {
        if (this.roleService.existsByName(role.getName())) {
            throw new IdInvalidException("Role name already exists.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PutMapping("")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInvalidException {
        if(this.roleService.fetchRoleById(role.getId()) == null) {
            throw new IdInvalidException("Role id not found.");
        }

        if (this.roleService.existsByName(role.getName())) {
            throw new IdInvalidException("Role name already exists.");
        }

        return ResponseEntity.ok(this.roleService.updateRole(role));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Update a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if(this.roleService.fetchRoleById(id) == null) {
            throw new IdInvalidException("Role id not found.");
        }

        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.fetchAllRoles(spec, pageable));
    }
}
