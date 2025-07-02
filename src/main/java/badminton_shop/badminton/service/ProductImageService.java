package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.ProductImage;
import badminton_shop.badminton.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public ProductImageService(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    public ProductImage createProductImage(ProductImage productImage) {
        return this.productImageRepository.save(productImage);
    }

    public void deleteImagesByProduct(Long productId) {
        List<ProductImage> images = productImageRepository.findAllByProductId(productId);
        productImageRepository.deleteAll(images);
    }

    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findAllByProductId(productId);
    }

    public void deleteImageById(Long id) {
        this.productImageRepository.deleteById(id);
    }
}
