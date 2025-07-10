package badminton_shop.badminton.domain.request.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqOrderItemDTO {
    private Long variantId;
    private int quantity;
    private double unitPrice;
}
