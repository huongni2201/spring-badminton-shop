package badminton_shop.badminton.domain.response.user;

import badminton_shop.badminton.utils.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
public class ResUpdateUserDTO {
    private long id;
    private String fullName;
    private String phone;
    private RoleUser role;
    private String address;
    private String avatar;
    private LocalDate dob;
    private GenderEnum gender;
    private Instant updatedAt;

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String role;
    }
}
