package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.response.user.ResCreateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUpdateUserDTO;
import badminton_shop.badminton.domain.response.user.ResUserDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import badminton_shop.badminton.service.UserService;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService) {
        this.userService = userService;
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


}
