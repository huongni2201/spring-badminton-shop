package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Brand;
import badminton_shop.badminton.service.BrandService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/api/v1/brands")
    @ApiMessage("Fetch all brands")
    public ResponseEntity<List<Brand>> fetchBrands() {
        return ResponseEntity.ok(this.brandService.getAllBrands());
    }
}
