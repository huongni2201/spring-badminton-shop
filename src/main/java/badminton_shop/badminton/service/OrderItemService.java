package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.*;
import badminton_shop.badminton.domain.request.order.ReqOrderItemDTO;
import badminton_shop.badminton.domain.response.order.ResOrderItemDTO;
import badminton_shop.badminton.repository.OrderItemRepository;
import badminton_shop.badminton.utils.exception.InvalidArgumentException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantService productVariantService;
    private final ProductService productService;

    private final ProductVariantAttributeService productVariantAttributeService;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            ProductVariantService productVariantService,
            ProductService productService,
            ProductVariantAttributeService productVariantAttributeService) {
        this.orderItemRepository = orderItemRepository;
        this.productVariantService = productVariantService;
        this.productService = productService;
        this.productVariantAttributeService = productVariantAttributeService;
    }

    public OrderItem save(ReqOrderItemDTO reqOrderItemDTO, Order order) throws InvalidArgumentException {
        ProductVariant productVariant = this.productVariantService.getProductVariantById(reqOrderItemDTO.getVariantId());

        if (productVariant == null) {
            throw new InvalidArgumentException("Không tìm thấy sản phẩm này!");
        }

        if (reqOrderItemDTO.getQuantity() <= 0) {
            throw new InvalidArgumentException("Số lượng phải lớn hơn 0!");
        }

        if (productVariant.getStockQuantity() < reqOrderItemDTO.getQuantity()) {
            throw new InvalidArgumentException("Không đủ số lượng tồn kho!");
        }

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productVariant(productVariant)
                .quantity(reqOrderItemDTO.getQuantity())
                .priceAtOrderTime(productVariant.getPrice())
                .build();

        productVariant.setStockQuantity(productVariant.getStockQuantity() - reqOrderItemDTO.getQuantity());
        this.productVariantService.save(productVariant);

        return this.orderItemRepository.save(orderItem);
    }

    public ResOrderItemDTO convertToResOrderItemDTO(OrderItem orderItem) {
        ProductVariant productVariant = orderItem.getProductVariant();
        Product product = productVariant.getProduct();

        Map<String, String> productVariantAttribute = productVariant.getProductVariantAttributeValues()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getProductVariantAttribute().getName(),
                        ProductVariantAttributeValue::getValue
                        ));

        return ResOrderItemDTO.builder()
                .productName(product.getName())
                .variantAttributeValues(productVariantAttribute)
                .unitPrice(orderItem.getPriceAtOrderTime())
                .quantity(orderItem.getQuantity())
                .thumbnailUrl(product.getImages()
                        .stream()
                        .filter(image -> image.isMainImage())
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null))
                .build();
    }
}
