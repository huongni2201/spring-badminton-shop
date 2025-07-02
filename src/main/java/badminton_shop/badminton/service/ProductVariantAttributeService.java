package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.ProductVariantAttribute;
import badminton_shop.badminton.repository.ProductVariantAttributeRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantAttributeService {
    private final ProductVariantAttributeRepository productVariantAttributeRepository;

    public ProductVariantAttributeService(ProductVariantAttributeRepository productVariantAttributeRepository) {
        this.productVariantAttributeRepository = productVariantAttributeRepository;
    }

    public ProductVariantAttribute getProductVariantAttributeById(Long id) {
        return this.productVariantAttributeRepository.findById(id).orElse(null);
    }

    public ProductVariantAttribute getProductVariantAttributeByName(String name) {
        return this.productVariantAttributeRepository.findByName(name);
    }
}
