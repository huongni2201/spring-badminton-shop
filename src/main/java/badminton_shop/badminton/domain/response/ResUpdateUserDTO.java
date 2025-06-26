package badminton_shop.badminton.domain.response;

import badminton_shop.badminton.domain.dto.RoleDTO;
import badminton_shop.badminton.utils.enums.GenderEnum;
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
    private RoleDTO role;
    private String address;
    private String avatar;
    private LocalDate dob;
    private GenderEnum gender;
    private Instant updatedAt;
}
