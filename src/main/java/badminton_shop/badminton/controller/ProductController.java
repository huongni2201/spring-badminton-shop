package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Product;
import badminton_shop.badminton.domain.RestResponse;
import badminton_shop.badminton.domain.dto.ResultPaginationDTO;
import badminton_shop.badminton.service.ProductService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<RestResponse<Product>> createProduct(@Valid @RequestBody Product product) {
        Product pro = this.productService.createProduct(product);
        RestResponse<Product> res = new RestResponse<>();
        res.setData(pro);
        res.setMessage("Product created successfully");
        res.setStatusCode(HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/products")
    @ApiMessage("fetch all products")
    public ResponseEntity<ResultPaginationDTO> fetchProducts(
            @Filter Specification<Product> spec, Pageable pageable) {
        return ResponseEntity.ok(this.productService.fetchProducts(spec, pageable));
    }

    @PutMapping("/products")
    public ResponseEntity<RestResponse<Product>> updateProduct(@Valid @RequestBody Product reqProduct) {
        Product product = this.productService.updateProduct(reqProduct);
        RestResponse<Product> res = new RestResponse<>();
        res.setData(product);
        res.setMessage("Product updated successfully");

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/products")
    public ResponseEntity<RestResponse<String>> deleteProductById(@RequestParam("id") Long id) {
        this.productService.deleteById(id);
        RestResponse<String> res = new RestResponse<>();
        res.setData("Product deleted successfully");

        return ResponseEntity.ok(res);
    }

}
