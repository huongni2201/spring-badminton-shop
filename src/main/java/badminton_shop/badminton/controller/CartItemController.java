package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Cart;
import badminton_shop.badminton.domain.CartItem;
import badminton_shop.badminton.domain.ProductVariant;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.cart.ReqCartItemDTO;
import badminton_shop.badminton.domain.response.cart.ResCartDTO;
import badminton_shop.badminton.service.CartItemService;
import badminton_shop.badminton.service.CartService;
import badminton_shop.badminton.service.ProductVariantService;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import badminton_shop.badminton.utils.exception.InvalidArgumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;
    private final UserService userService;
    private final CartService cartService;
    private final ProductVariantService productVariantService;

    public CartItemController(
            CartItemService cartItemService,
            UserService userService,
            CartService cartService,
            ProductVariantService productVariantService
    ) {
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.cartService = cartService;
        this.productVariantService = productVariantService;
    }

    @PostMapping()
    @ApiMessage("Add to cart")
    public ResponseEntity<ResCartDTO> addToCart(@RequestBody ReqCartItemDTO reqCartItemDTO) throws IdInvalidException, InvalidArgumentException {
        User currentUser = this.userService.fetchUserLogin();

        Cart cart = cartService.fetchCartByUser(currentUser);
        if (cart == null) {
            cart = cartService.createCart(currentUser.getUserId());
        }

        ProductVariant variant = productVariantService.getProductVariantById(reqCartItemDTO.getVariantId());

        this.cartItemService.addOrUpdateCartItem(reqCartItemDTO, cart, variant);
        return ResponseEntity.ok(cartService.convertToResCartDTO(cart));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update cart item")
    public ResponseEntity<ResCartDTO> updateCartItem(
            @PathVariable("id") Long id,
            @RequestBody ReqCartItemDTO reqCartItemDTO)
            throws IdInvalidException, InvalidArgumentException {
        User currentUser = this.userService.fetchUserLogin();

        Cart cart = cartService.fetchCartByUser(currentUser);
        if (cart == null) {
            throw new IdInvalidException("Cart not found!");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IdInvalidException("Cart item not found!"));

        cartItem.setQuantity(reqCartItemDTO.getQuantity());
        cartItemService.updateCartItem(cartItem);

        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete cart item")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long id) {
        this.cartItemService.deleteCartItemById(id);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("")
    @ApiMessage("Clear all items in the cart")
    public ResponseEntity<Void> clearCart() throws IdInvalidException {
        User currentUser = userService.fetchUserLogin();
        Cart cart = cartService.fetchCartByUser(currentUser);

        if (cart != null) {
            cartItemService.deleteAllByCartId(cart.getId());
        }

        return ResponseEntity.noContent().build(); // Trả 204 cho hành động xóa
    }



}
