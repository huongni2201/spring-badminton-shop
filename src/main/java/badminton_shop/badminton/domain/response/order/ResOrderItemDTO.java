package badminton_shop.badminton.domain.response.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class ResOrderItemDTO {
    private String productName;

    private Map<String, String> variantAttributeValues;
    private int quantity;
    private double unitPrice;
    private String thumbnailUrl;
}
