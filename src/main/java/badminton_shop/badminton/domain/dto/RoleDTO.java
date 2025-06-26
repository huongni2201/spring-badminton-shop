package badminton_shop.badminton.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoleDTO {
    private long id;
    private String role;
}
