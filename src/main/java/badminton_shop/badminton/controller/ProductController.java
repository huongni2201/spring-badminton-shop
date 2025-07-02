package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Product;
import badminton_shop.badminton.domain.request.product.ReqProductDTO;
import badminton_shop.badminton.domain.response.product.ResProductDetailsDTO;
import badminton_shop.badminton.domain.response.RestResponse;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.service.ProductService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    @ApiMessage("Create new product")
    public ResponseEntity<RestResponse<ReqProductDTO>> createProduct(@Valid @RequestBody ReqProductDTO reqProductDTO) {
        ReqProductDTO pro = this.productService.convertToReqProductDTO(productService.createProduct(reqProductDTO));
        RestResponse<ReqProductDTO> res = new RestResponse<>();
        res.setData(pro);
        res.setMessage("Product created successfully");
        res.setStatusCode(HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/products")
    @ApiMessage("Fetch all products")
    public ResponseEntity<ResultPaginationDTO> fetchProducts(
            @Filter Specification<Product> spec, Pageable pageable) {
        return ResponseEntity.ok(this.productService.fetchProducts(spec, pageable));
    }

    @GetMapping("/admin/products")
    @ApiMessage("Fetch all products for admin")
    public ResponseEntity<ResultPaginationDTO> fetchAdminProducts(
            @Filter Specification<Product> spec, Pageable pageable) {
        return ResponseEntity.ok(this.productService.fetchAdminProducts(spec, pageable));
    }

    @GetMapping("/products/{id}")
    @ApiMessage("Fetch product by id")
    public ResponseEntity<ResProductDetailsDTO> fetchProductById(
            @PathVariable("id") Long id) throws IdInvalidException {
        Product product = this.productService.getProductById(id);
        if (product == null) {
            throw new IdInvalidException("Product with id = " + id + " does not exist.");
        }
        return ResponseEntity.ok(this.productService.convertToResProductDetailsDTO(product));
    }


    @PutMapping("/products/{id}")
    @ApiMessage("Update product")
    public ResponseEntity<RestResponse<Product>> updateProduct(
            @Valid @RequestBody ReqProductDTO reqProduct,
            @PathVariable("id") Optional<Long> idOptional)
            throws IdInvalidException {
        if (!idOptional.isEmpty()) {
            Product product = this.productService.updateProduct(idOptional.get(), reqProduct);
            RestResponse<Product> res = new RestResponse<>();
            res.setData(product);
            res.setMessage("Product updated successfully");

            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/products/{id}")
    @ApiMessage("Delete product by id")
    public ResponseEntity<RestResponse<String>> deleteProductById(@PathVariable("id") Optional<Long> idOptional) {
        this.productService.deleteById(idOptional.orElse(0L));
        RestResponse<String> res = new RestResponse<>();
        res.setData("Product deleted successfully");

        return ResponseEntity.ok(res);
    }

}
