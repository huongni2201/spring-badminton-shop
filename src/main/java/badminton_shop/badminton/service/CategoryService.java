package badminton_shop.badminton.service;

import java.util.List;

import badminton_shop.badminton.domain.Category;
import org.springframework.stereotype.Service;
import badminton_shop.badminton.repository.CategoryRepository;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }
}
