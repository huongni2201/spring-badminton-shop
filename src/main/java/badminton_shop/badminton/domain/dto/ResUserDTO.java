package badminton_shop.badminton.domain.dto;

import badminton_shop.badminton.utils.enums.GenderEnum;
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
    private RoleDTO role;
    private String address;
    private String avatar;
    private LocalDate dob;
    private GenderEnum gender;
    private Instant createdAt;
    private Instant updatedAt;
}
