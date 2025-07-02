package badminton_shop.badminton.domain.response.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserLogin {
        private Long id;
        private String email;
        private String fullName;
        private String avatar;
        private String role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccout {
         private UserLogin user;
    }


}
