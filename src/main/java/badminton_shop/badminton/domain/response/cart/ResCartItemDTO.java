package badminton_shop.badminton.domain.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class ResCartItemDTO {
    private Long cartItemId;
    private String productName;
    private String productImage;

    private Long variantId;
    private String sku;


    private Map<String, String> variantAttributeValues;
    private int quantity;
    private int stockQuantity;
    private double unitPrice;
}
