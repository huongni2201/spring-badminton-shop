package badminton_shop.badminton.domain.response.product;


import badminton_shop.badminton.utils.constant.ProductStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResProductAdmin {
    private Long id;
    private String name;
    private int stockQuantity;
    private ProductStatus status;
    private Long categoryId;
    private Long brandId;
    private String categoryName;
    private String brandName;
    private String description;
    private Instant createdAt;
    private Instant updateAt;

    private List<String> images;

    private List<ProductVariantDTO> variants;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantDTO {
        private long id;
        private String sku;
        private double price;
        private int stockQuantity;
        private List<VariantAttributeValueDTO> attributeValues;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantAttributeValueDTO {
        private long id;
        private String name;
    }
}
