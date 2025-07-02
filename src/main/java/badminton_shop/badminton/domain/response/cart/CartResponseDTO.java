package badminton_shop.badminton.domain.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CartResponseDTO {
    private Long cartId;
    private CartUserDTO userDTO;

    private List<CartItemResponseDTO> items;

    private double totalPrice;
    private double shippingPrice;

    @Getter
    @Setter
    @Builder
    public static class CartUserDTO {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
    }
}
