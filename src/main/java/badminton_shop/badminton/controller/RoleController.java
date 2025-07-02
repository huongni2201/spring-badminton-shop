package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.service.RoleService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    @ApiMessage("Fetch all roles")
    public ResponseEntity<List<Role>> fetchRoles() {
        return ResponseEntity.ok(this.roleService.fetchAllRoles());
    }
}
