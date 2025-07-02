package badminton_shop.badminton.domain.response.cart;

import java.util.Map;

public class CartItemResponseDTO {
    private Long productId;
    private String productName;
    private String productImage;

    private Long variantId;
    private String sku;


    private Map<String, String> variantAttributeValues;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
}
