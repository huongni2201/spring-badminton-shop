package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Product;
import badminton_shop.badminton.domain.ProductVariant;
import badminton_shop.badminton.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    public ProductVariant save(ProductVariant productVariant) {
        return this.productVariantRepository.save(productVariant);
    }

    public ProductVariant getProductVariantById(Long id) {
        return this.productVariantRepository.findById(id).orElse(null);
    }

    public void deleteProductVariantById(Long id) {
        this.productVariantRepository.deleteById(id);
    }

    public void deleteByProduct(Long productId) {
        List<ProductVariant> variants = productVariantRepository.findAllByProductId(productId);
        productVariantRepository.deleteAll(variants);
    }

}
