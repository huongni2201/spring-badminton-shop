package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.*;
import badminton_shop.badminton.domain.request.cart.ReqCartItemDTO;
import badminton_shop.badminton.domain.response.cart.ResCartItemDTO;
import badminton_shop.badminton.repository.CartItemRepository;
import badminton_shop.badminton.utils.exception.InvalidArgumentException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public void deleteCartItemById(Long id) {
        this.cartItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByCartId(Long cartId) {
        this.cartItemRepository.deleteByCartId(cartId);
    }

    public void updateCartItem(CartItem cartItem) throws InvalidArgumentException {
        if (cartItem.getQuantity() <= 0) {
            throw new InvalidArgumentException("Quantity must be greater than 0");
        }

        if (cartItem.getQuantity() > cartItem.getVariant().getStockQuantity()) {
            throw new InvalidArgumentException("Quantity exceeds available stock!");
        }

        this.cartItemRepository.save(cartItem);
    }


    public void addOrUpdateCartItem(ReqCartItemDTO dto, Cart cart, ProductVariant variant) throws InvalidArgumentException {
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());

            if (existingItem.getQuantity() > variant.getStockQuantity()) {
                throw new InvalidArgumentException("Quantity exceeds available stock!");
            }

            this.cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setVariant(variant);
            newItem.setQuantity(dto.getQuantity());
            cart.getItems().add(newItem);
            this.cartItemRepository.save(newItem);
        }
    }

    public ResCartItemDTO convertToResCartItemDTO(CartItem cartItem) {
        ResCartItemDTO res = ResCartItemDTO.builder().build();

        Map<String, String> variantAttributes = cartItem.getVariant().getProductVariantAttributeValues().stream()
                .collect(Collectors.toMap(
                        v -> v.getProductVariantAttribute().getName(),
                        ProductVariantAttributeValue::getValue
                ));

        List<ProductImage> images = cartItem.getVariant().getProduct().getImages();
        String productImageUrl = images != null && !images.isEmpty() ? images.get(0).getImageUrl() : null;

        res.setCartItemId(cartItem.getId());
        res.setVariantId(cartItem.getVariant().getId());
        res.setProductName(cartItem.getVariant().getProduct().getName());
        res.setProductImage(productImageUrl);
        res.setSku(cartItem.getVariant().getSku());
        res.setUnitPrice(cartItem.getVariant().getPrice());
        res.setQuantity(cartItem.getQuantity());
        res.setVariantAttributeValues(variantAttributes);
        res.setStockQuantity(cartItem.getVariant().getStockQuantity());
        return res;
    }
}
