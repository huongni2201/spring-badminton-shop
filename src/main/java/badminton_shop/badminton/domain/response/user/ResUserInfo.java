package badminton_shop.badminton.domain.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ResUserInfo {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private LocalDate dob;
    private String gender;
    private String role;
    private String address;

    private int totalOrder;
    private double totalPrice;

    private Instant createdAt;
}
