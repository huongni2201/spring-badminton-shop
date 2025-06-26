package badminton_shop.badminton.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantAttributeDTO {
    private String name; // Ví dụ: "Weight", "Size", "Color"
    private String slug;
    private String value; // Ví dụ: "3U", "42", "Red"
}
