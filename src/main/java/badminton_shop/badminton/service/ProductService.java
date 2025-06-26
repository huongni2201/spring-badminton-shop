package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Product;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import badminton_shop.badminton.repository.ProductRepository;

import java.util.Optional;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ResultPaginationDTO fetchProducts(Specification<Product> spec, Pageable pageable) {
        Page<Product> products = this.productRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(products.getTotalPages());
        meta.setTotalItems(products.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(products.getContent());

        return resultPaginationDTO;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Optional<Product> optionalProduct = productRepository.findById(product.getId());

        if (optionalProduct.isPresent()) {
            Product currentProduct = optionalProduct.get();
            currentProduct.setName(product.getName());
            currentProduct.setDescription(product.getDescription());
            currentProduct.setCategory(product.getCategory());
            currentProduct.setBrand(product.getBrand());

            return productRepository.save(currentProduct);
        }
        return null;
    }

    public void deleteById(Long id) {
        this.productRepository.deleteById(id);
    }
}
