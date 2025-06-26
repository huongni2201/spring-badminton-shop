package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.ReqLoginDTO;
import badminton_shop.badminton.domain.response.ResLoginDTO;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.SecurityUtil;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${badminton.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //nạp thông tin (nếu xử lý thành công) vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create token
        ResLoginDTO resLoginDTO = new ResLoginDTO();

        User currentUserDB = this.userService.findByEmail(reqLoginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                    .id(currentUserDB.getUserId())
                    .avatar(currentUserDB.getAvatar())
                    .email(currentUserDB.getEmail())
                    .fullName(currentUserDB.getFullName())
                    .role(currentUserDB.getRole().getName().name())
                    .build();
            resLoginDTO.setUser(userLogin);

        }
        String access_token=  this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());


        resLoginDTO.setAccessToken(access_token);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(reqLoginDTO.getUsername(), resLoginDTO);

        // update user
        this.userService.updateUserToken(refresh_token, reqLoginDTO.getUsername() );

        // set cookies
         ResponseCookie resCookie = ResponseCookie
                 .from("refresh_token", refresh_token)
                 .httpOnly(true)
                 .path("/")
                 .maxAge(refreshTokenExpiration)
                 .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account message")
    public ResponseEntity<ResLoginDTO.UserGetAccout> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.findByEmail(email);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccout getAccount = new ResLoginDTO.UserGetAccout();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getUserId());
            userLogin.setAvatar(currentUserDB.getAvatar());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setFullName(currentUserDB.getFullName());
            userLogin.setRole(currentUserDB.getRole().getName().name());

            getAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(getAccount);
    }

    @GetMapping("/auth/refresh")
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
        ResLoginDTO resLoginDTO = new ResLoginDTO();

        User currentUserDB = this.userService.findByEmail(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                    .id(currentUserDB.getUserId())
                    .avatar(currentUserDB.getAvatar())
                    .email(currentUserDB.getEmail())
                    .fullName(currentUserDB.getFullName())
                    .role(currentUserDB.getRole().getName().name())
                    .build();
            resLoginDTO.setUser(userLogin);

        }
        String access_token=  this.securityUtil.createAccessToken(email, resLoginDTO.getUser());


        resLoginDTO.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token is invalid!");
        }
        this.userService.updateUserToken(null, email);

        ResponseCookie deleteSpringCookies = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookies.toString())
                .build();

    }
}
