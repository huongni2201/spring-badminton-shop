package badminton_shop.badminton.domain.mapper;

import badminton_shop.badminton.domain.Product;
import badminton_shop.badminton.domain.ProductImage;
import badminton_shop.badminton.domain.ProductVariant;
import badminton_shop.badminton.domain.VariantAttributeValue;
import badminton_shop.badminton.domain.dto.ProductDTO;
import badminton_shop.badminton.domain.dto.ProductVariantDTO;
import badminton_shop.badminton.domain.dto.VariantAttributeDTO;

import java.util.List;
import java.util.stream.Collectors;


public class ProductMapper {

	public static ProductDTO toDTO(Product product) {
		String mainImage = product.getImages().stream()
				.filter(ProductImage::isMainImage)
				.map(ProductImage::getImageUrl)
				.findFirst()
				.orElse(null);

		List<String> subImages = product.getImages().stream()
				.filter(img -> !img.isMainImage())
				.map(ProductImage::getImageUrl)
				.collect(Collectors.toList());

		List<ProductVariantDTO> variantDTOs = product.getProductVariants().stream()
				.map(ProductMapper::toProductVariantDTO)
				.collect(Collectors.toList());

		return ProductDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.brandName(product.getBrand().getName())
				.categoryName(product.getCategory().getName())
				.mainImageUrl(mainImage)
				.subImageUrls(subImages)
				.status(product.getStatus())
				.variants(variantDTOs)
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.build();
	}

	public static ProductVariantDTO toProductVariantDTO(ProductVariant productVariant) {
		List<VariantAttributeDTO> attributeDTOs = productVariant.getAttributeValues().stream()
				.map(ProductMapper::toVariantAttributeDTO)
				.collect(Collectors.toList());

		return ProductVariantDTO.builder()
				.id(productVariant.getId())
				.sku(productVariant.getSku())
				.price(productVariant.getPrice())
				.stockQuantity(productVariant.getStockQuantity())
				.attributes(attributeDTOs)
				.createdAt(productVariant.getCreatedAt())
				.updatedAt(productVariant.getUpdatedAt())
				.build();
	}

	public static VariantAttributeDTO toVariantAttributeDTO(VariantAttributeValue variantAttributeValue) {
		return VariantAttributeDTO.builder()
				.name(variantAttributeValue.getVariantAttribute().getName())
				.slug(variantAttributeValue.getVariantAttribute().getSlug())
				.value(variantAttributeValue.getValue())
				.build();
	}
}
