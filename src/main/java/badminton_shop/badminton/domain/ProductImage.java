package badminton_shop.badminton.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(optional = false)
    @JsonBackReference
    private Product product;

    private boolean isMainImage;

    @Column(length = 255)
    private String imageAlt;

    private int imageOrder;

    @Column(updatable = false)
    private Instant uploadedAt;

    @PrePersist
    protected void onUpload() {
        this.uploadedAt = Instant.now();
    }

}
