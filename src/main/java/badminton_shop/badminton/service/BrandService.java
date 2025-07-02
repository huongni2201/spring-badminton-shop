package badminton_shop.badminton.service;

import java.util.List;

import badminton_shop.badminton.domain.Brand;
import org.springframework.stereotype.Service;
import badminton_shop.badminton.repository.BrandRepository;


@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return this.brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        return this.brandRepository.findById(id).orElse(null);
    }
}
