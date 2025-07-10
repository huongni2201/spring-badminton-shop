package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.Cart;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.response.cart.ResCartDTO;
import badminton_shop.badminton.service.CartService;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping()
    @ApiMessage("Fetch all cart items for user")
    public ResponseEntity<ResCartDTO> fetchCarts() throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();

        Cart cart = this.cartService.fetchCartByUser(currentUser);
        return ResponseEntity.ok(this.cartService.convertToResCartDTO(cart));
    }






}
