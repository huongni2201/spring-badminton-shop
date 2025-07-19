package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Order;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.response.user.ResCreateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUpdateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUserDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.domain.response.user.ResUserInfo;
import badminton_shop.badminton.service.OrderService;
import badminton_shop.badminton.service.RoleService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import badminton_shop.badminton.service.UserService;

import java.util.List;


@RestController
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OrderService orderService;
    private final RoleService roleService;

    public UserController(UserService userService, OrderService orderService, RoleService roleService) {
        this.userService = userService;
        this.orderService = orderService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/api/v1/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchUsers(spec, pageable));
    }

    @GetMapping("/api/v1/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User fetchUserById = this.userService.fetchUserById(id);
        if (fetchUserById == null) {
            throw new IdInvalidException("User with id = " + id + " does not exist.");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(fetchUserById));
    }

    @PostMapping("/api/v1/users")
    @ApiMessage("create user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody @Valid User postUser) throws IdInvalidException {

        if (this.userService.isEmailExist(postUser.getEmail())) {
            throw new IdInvalidException("Email already exist...");
        }

        postUser.setPassword(bCryptPasswordEncoder.encode(postUser.getPassword()));

        if (postUser.getRole() == null) {
            postUser.setRole(this.roleService.fetchRoleByName("USER"));
        }

        User userCreated = this.userService.handleCreateUser(postUser);
        userCreated.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(userCreated));
    }

    @PutMapping("/api/v1/users/{id}")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@PathVariable("id") Long id, @RequestBody User reqUser) throws IdInvalidException {
        reqUser.setUserId(id);
        User updateUser = this.userService.handleUpdateUser(reqUser);
        if (updateUser == null) {
            throw new IdInvalidException("User with id = " + reqUser.getUserId() + " does not exist.");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updateUser));
    }

    @DeleteMapping("/api/v1/users/{id}")
    @ApiMessage("delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {

        if (this.userService.fetchUserById(id) == null) {
            throw new IdInvalidException("User with id = " + id + " does not exist.");
        }
        this.userService.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/v1/users/info")
    @ApiMessage("Fetch user info")
    public ResponseEntity<ResUserInfo> fetchUserInfo() throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();
        if (currentUser == null) {
            throw new IdInvalidException("User not login...");
        }
        List<Order> orders = this.orderService.fetchOrdersByUser(currentUser);

        ResUserInfo resUserInfo = ResUserInfo.builder()
                .id(currentUser.getUserId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .dob(currentUser.getDob())
                .phone(currentUser.getPhone())
                .address(currentUser.getAddress())
                .avatar(currentUser.getAvatar())
                .gender(currentUser.getGender() != null ? currentUser.getGender().name() : null)
                .totalOrder(orders.size())
                .totalPrice(orders.stream().mapToDouble(Order::getTotalPrice).sum())
                .createdAt(currentUser.getCreatedAt())
                .build();

        return ResponseEntity.ok(resUserInfo);
    }


}
