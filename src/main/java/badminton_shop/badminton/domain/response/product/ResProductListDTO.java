package badminton_shop.badminton.domain.response.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResProductListDTO {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private double minPrice;
    private String categoryName;
    private String brandName;
}
