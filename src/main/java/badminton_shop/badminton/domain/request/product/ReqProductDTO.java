package badminton_shop.badminton.domain.request.product;

import badminton_shop.badminton.utils.constant.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReqProductDTO {
    private String name;
    private Long categoryId;
    private Long brandId;
    private String description;
    private ProductStatus status;
    private List<String> images;
    private List<ReqVariantDTO> variants;

    @Getter
    @Setter
    @Builder
    public static class ReqVariantDTO {
        private String color;
        private String size;
        private double price;
        private int stockQuantity;
    }

}
