package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.*;
import badminton_shop.badminton.domain.request.product.ReqProductDTO;
import badminton_shop.badminton.domain.response.product.ResProductDetailsDTO;
import badminton_shop.badminton.domain.response.product.ResProductListDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.repository.ProductRepository;
import badminton_shop.badminton.utils.MyUtils;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductImageService productImageService;
    private final ProductVariantService productVariantService;
    private final ProductVariantAttributeService productVariantAttributeService;

    public ProductService(ProductRepository productRepository,
                          CategoryService categoryService,
                          BrandService brandService,
                          ProductImageService productImageService,
                          ProductVariantService productVariantService,
                          ProductVariantAttributeService productVariantAttributeService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.productImageService = productImageService;
        this.productVariantService = productVariantService;
        this.productVariantAttributeService = productVariantAttributeService;
    }

    public ResultPaginationDTO fetchProducts(Specification<Product> spec, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        Page<ResProductListDTO> mapped = productPage.map(product -> {
            String thumbnail = product.getImages().stream()
                    .filter(ProductImage::isMainImage)
                    .map(ProductImage::getImageUrl)
                    .findFirst().orElse(null);

            double minPrice = product.getProductVariants().stream()
                    .mapToDouble(ProductVariant::getPrice)
                    .min().orElse(0);

            return ResProductListDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .categoryName(product.getCategory().getName())
                    .brandName(product.getBrand().getName())
                    .thumbnailUrl(thumbnail)
                    .minPrice(minPrice)
                    .build();
        });

        return ResultPaginationDTO.builder()
                .meta(buildMeta(productPage, pageable))
                .result(mapped.getContent())
                .build();
    }

    public ResultPaginationDTO fetchAdminProducts(Specification<Product> spec, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        Page<ResProductDetailsDTO> mapped = productPage.map(this::convertToResProductDetailsDTO);

        return ResultPaginationDTO.builder()
                .meta(buildMeta(productPage, pageable))
                .result(mapped.getContent())
                .build();
    }

    public Product getProductById(Long id) throws IdInvalidException {
        return productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Product not found with id: " + id));
    }

    public Product createProduct(ReqProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        product.setBrand(brandService.getBrandById(dto.getBrandId()));
        product = productRepository.save(product);

        for (int i = 0; i < dto.getImages().size(); i++) {
            ProductImage image = buildProductImage(product, dto.getImages().get(i), i);
            productImageService.createProductImage(image);
        }

        ProductVariantAttribute colorAttr = productVariantAttributeService.getProductVariantAttributeByName("color");
        ProductVariantAttribute sizeAttr = productVariantAttributeService.getProductVariantAttributeByName("size");

        Product finalProduct = product;
        List<ProductVariant> variants = dto.getVariants().stream()
                .map(v -> buildProductVariant(finalProduct, v, colorAttr, sizeAttr))
                .collect(Collectors.toCollection(ArrayList::new));
        product.setProductVariants(variants);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ReqProductDTO dto) throws IdInvalidException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sản phẩm với id: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        product.setBrand(brandService.getBrandById(dto.getBrandId()));

        product.getImages().clear();
        for (int i = 0; i < dto.getImages().size(); i++) {
            product.getImages().add(buildProductImage(product, dto.getImages().get(i), i));
        }

        product.getProductVariants().clear();
        ProductVariantAttribute colorAttr = productVariantAttributeService.getProductVariantAttributeByName("color");
        ProductVariantAttribute sizeAttr = productVariantAttributeService.getProductVariantAttributeByName("size");

        for (ReqProductDTO.ReqVariantDTO v : dto.getVariants()) {
            product.getProductVariants().add(buildProductVariant(product, v, colorAttr, sizeAttr));
        }

        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public ResProductDetailsDTO convertToResProductDetailsDTO(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .sorted(Comparator.comparing(img -> !img.isMainImage()))
                .map(ProductImage::getImageUrl).toList();

        List<ResProductDetailsDTO.ProductVariantDTO> variants = product.getProductVariants().stream()
                .map(variant -> ResProductDetailsDTO.ProductVariantDTO.builder()
                        .id(variant.getId())
                        .sku(variant.getSku())
                        .price(variant.getPrice())
                        .stockQuantity(variant.getStockQuantity())
                        .attributeValues(variant.getProductVariantAttributeValues().stream()
                                .map(attr -> ResProductDetailsDTO.VariantAttributeValueDTO.builder()
                                        .id(attr.getId())
                                        .name(attr.getProductVariantAttribute().getName())
                                        .value(attr.getValue())
                                        .build())
                                .toList())
                        .build())
                .toList();

        int totalStock = product.getProductVariants().stream()
                .mapToInt(ProductVariant::getStockQuantity).sum();

        return ResProductDetailsDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .status(product.getStatus())
                .categoryName(product.getCategory().getName())
                .brandName(product.getBrand().getName())
                .brandId(product.getBrand().getId())
                .categoryId(product.getCategory().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .stockQuantity(totalStock)
                .images(imageUrls)
                .variants(variants)
                .build();
    }

    public ReqProductDTO convertToReqProductDTO(Product product) {
        List<String> imageUrls = product.getImages() != null ?
                product.getImages().stream().map(ProductImage::getImageUrl).toList() : new ArrayList<>();

        List<ReqProductDTO.ReqVariantDTO> variants = product.getProductVariants() != null ?
                product.getProductVariants().stream().map(variant -> {
                    String color = null, size = null;
                    for (ProductVariantAttributeValue value : variant.getProductVariantAttributeValues()) {
                        String name = value.getProductVariantAttribute().getName().toLowerCase();
                        if ("color".equals(name)) color = value.getValue();
                        else if ("size".equals(name)) size = value.getValue();
                    }
                    return ReqProductDTO.ReqVariantDTO.builder()
                            .color(color).size(size)
                            .price(variant.getPrice())
                            .stockQuantity(variant.getStockQuantity())
                            .build();
                }).toList() : new ArrayList<>();

        return ReqProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .status(product.getStatus())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .images(imageUrls)
                .variants(variants)
                .build();
    }

    private ResultPaginationDTO.Meta buildMeta(Page<?> page, Pageable pageable) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(page.getTotalPages());
        meta.setTotalItems(page.getTotalElements());
        return meta;
    }

    private ProductImage buildProductImage(Product product, String imageUrl, int index) {
        return ProductImage.builder()
                .imageAlt(product.getName())
                .imageUrl(imageUrl)
                .product(product)
                .isMainImage(index == 0)
                .build();
    }

    private ProductVariant buildProductVariant(Product product, ReqProductDTO.ReqVariantDTO v,
                                               ProductVariantAttribute colorAttr, ProductVariantAttribute sizeAttr) {

        ProductVariant variant = ProductVariant.builder()
                .price(v.getPrice())
                .stockQuantity(v.getStockQuantity())
                .product(product)
                .sku(MyUtils.generateSKU(product.getName(), v.getColor(), v.getSize()))
                .build();

        List<ProductVariantAttributeValue> variantAttrValues = new ArrayList<>();
        variantAttrValues.add(ProductVariantAttributeValue.builder()
                .productVariantAttribute(colorAttr)
                .value(v.getColor())
                .productVariant(variant)
                .build());
        variantAttrValues.add( ProductVariantAttributeValue.builder()
                .productVariantAttribute(sizeAttr)
                .value(v.getSize())
                .productVariant(variant)
                .build());

        variant.setProductVariantAttributeValues(variantAttrValues);

        return variant;
    }





}
