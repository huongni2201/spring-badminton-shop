package badminton_shop.badminton.domain.response.user;


import badminton_shop.badminton.utils.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

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
        private String phone;
        private String avatar;
        private String role;
        private String dob;
        private GenderEnum gender;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccout {
         private UserLogin user;
    }


}
