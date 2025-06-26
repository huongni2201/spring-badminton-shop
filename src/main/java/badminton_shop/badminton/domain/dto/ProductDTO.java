package badminton_shop.badminton.domain.dto;

import java.time.Instant;
import java.util.List;


import badminton_shop.badminton.utils.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;

    private String categoryName;

    private String brandName;

    private ProductStatus status;

    private String mainImageUrl;
    private List<String> subImageUrls;

    private Instant createdAt;
    private Instant updatedAt;

    private List<ProductVariantDTO> variants;
}
