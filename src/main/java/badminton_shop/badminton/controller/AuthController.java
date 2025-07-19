package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Role;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.ReqLoginDTO;
import badminton_shop.badminton.domain.response.RestResponse;
import badminton_shop.badminton.domain.response.user.ResCreateUserDTO;
import badminton_shop.badminton.domain.response.user.ResLoginDTO;
import badminton_shop.badminton.service.MailService;
import badminton_shop.badminton.service.RoleService;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.SecurityUtil;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.constant.EmailType;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${badminton.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Value("${badminton.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            RoleService roleService,
            MailService mailService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.roleService = roleService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
        try {
            // 1. Xác thực thông tin đăng nhập
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Lấy người dùng từ DB
            User user = userService.findByEmail(reqLoginDTO.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(RestResponse.unauthorized("Người dùng không tồn tại"));
            }

            // 3. Tạo thông tin phản hồi
            ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                    .id(user.getUserId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

            ResLoginDTO resLoginDTO = ResLoginDTO.builder()
                    .user(userLogin)
                    .build();

            // 4. Tạo token
            String accessToken = securityUtil.createAccessToken(user.getEmail(), resLoginDTO);
            String refreshToken = securityUtil.createRefreshToken(user.getEmail(), resLoginDTO);
            resLoginDTO.setAccessToken(accessToken);

            // 5. Cập nhật refresh token trong DB
            userService.updateUserToken(refreshToken, user.getEmail());

            // 6. Gửi access token qua cookie
            ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                    .httpOnly(false)
                    .secure(true)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(accessTokenExpiration)
                    .build();

            // 7. Gửi refresh token qua cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(refreshTokenExpiration)
                    .build();

            // 7. Trả về thành công
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(RestResponse.success(resLoginDTO, "Đăng nhập thành công"));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RestResponse.unauthorized("Tên đăng nhập hoặc mật khẩu không đúng"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestResponse.internalError("Lỗi hệ thống trong quá trình đăng nhập"));
        }
    }


    @GetMapping("/account")
    @ApiMessage("fetch account message")
    public ResponseEntity<ResLoginDTO.UserGetAccout> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        User currentUserDB = this.userService.findByEmail(email);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccout getAccount = new ResLoginDTO.UserGetAccout();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getUserId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setFullName(currentUserDB.getFullName());
            userLogin.setRole(currentUserDB.getRole());

            getAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(getAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refresh_token
    ) throws IdInvalidException {
        Jwt decodedToken =  this.securityUtil.checkValidRefreshToken(refresh_token);

        String email = decodedToken.getSubject();
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token is invalid");
        }

        // create token
        ResLoginDTO resLoginDTO =  ResLoginDTO.builder().build();

        User currentUserDB = this.userService.findByEmail(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                    .id(currentUserDB.getUserId())
                    .email(currentUserDB.getEmail())
                    .fullName(currentUserDB.getFullName())
                    .role(currentUserDB.getRole())
                    .build();
            resLoginDTO.setUser(userLogin);

        }
        String newAccessToken=  this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(newAccessToken);

        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update user
        this.userService.updateUserToken(newRefreshToken, email);

        // set cookies
        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(accessTokenExpiration)
                .build();

        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token is invalid!");
        }
        this.userService.updateUserToken(null, email);

        ResponseCookie clearAccess = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .build();

    }

    @PostMapping("/register")
    @ApiMessage("create user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody @Valid User postUser) throws IdInvalidException {

        if (this.userService.isEmailExist(postUser.getEmail())) {
            throw new IdInvalidException("Email already exist...");
        }

        postUser.setPassword(passwordEncoder.encode(postUser.getPassword()));

        if (postUser.getRole() == null) {
            postUser.setRole(this.roleService.fetchRoleByName("USER"));
        }

        User userCreated = this.userService.handleCreateUser(postUser);

        if (userCreated != null) {
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("fullName", userCreated.getFullName());

                mailService.sendEmail(userCreated.getEmail(), EmailType.REGISTER, variables);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        userCreated.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(userCreated));
    }
}
