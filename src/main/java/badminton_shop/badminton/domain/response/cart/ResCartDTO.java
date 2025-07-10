package badminton_shop.badminton.domain.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResCartDTO {
    private Long cartId;
    private ResCartUserDTO userDTO;

    private List<ResCartItemDTO> items;

    private double totalPrice;

    @Getter
    @Setter
    @Builder
    public static class ResCartUserDTO {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
    }
}
