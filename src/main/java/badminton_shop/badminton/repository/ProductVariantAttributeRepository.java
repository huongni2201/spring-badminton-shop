package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.ProductVariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantAttributeRepository extends JpaRepository<ProductVariantAttribute, Long> {
    ProductVariantAttribute findByName(String name);
}
