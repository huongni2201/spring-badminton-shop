package badminton_shop.badminton.domain.request.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqCartItemDTO {
    private int quantity;
    private Long variantId;
    private double unitPrice;
}
