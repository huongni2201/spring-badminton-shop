package badminton_shop.badminton.domain.response.user;

import badminton_shop.badminton.utils.constant.GenderEnum;
import badminton_shop.badminton.utils.constant.RoleName;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private RoleName role;
    private String address;
    private String avatar;
    private LocalDate dob;
    private GenderEnum gender;
    private Instant createdAt;
}
