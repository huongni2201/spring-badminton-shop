package badminton_shop.badminton.controller;


import badminton_shop.badminton.domain.Category;
import badminton_shop.badminton.service.CategoryService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/v1/categories")
    @ApiMessage("fetch all categories")
    public ResponseEntity<List<Category>> fetchCategories() {

        List<Category> categories = this.categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

}
