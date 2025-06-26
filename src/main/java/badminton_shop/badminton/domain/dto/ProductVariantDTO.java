package badminton_shop.badminton.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private String sku; // mã riêng cho biến thể, ví dụ: YONEX-3U-BLACK-42

    private double price;
    private int stockQuantity;

    // Ví dụ: "3U", "4U" cho vợt, "42", "43" cho giày
    private List<VariantAttributeDTO> attributes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
