package badminton_shop.badminton.domain.response.user;

import badminton_shop.badminton.utils.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private RoleUser role;
    private String address;
    private String avatar;
    private LocalDate dob;
    private GenderEnum gender;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String role;
    }
}
