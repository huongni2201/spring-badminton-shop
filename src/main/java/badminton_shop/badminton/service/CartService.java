package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Cart;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.response.cart.ResCartDTO;
import badminton_shop.badminton.domain.response.cart.ResCartItemDTO;
import badminton_shop.badminton.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemService cartItemService;

    public CartService(
            CartRepository cartRepository,
            UserService userService,
            CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.cartItemService = cartItemService;
    }

    public Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUser(this.userService.fetchUserById(userId));
        return this.cartRepository.save(cart);
    }

    public Cart fetchCartByUser(User user) {
        return cartRepository.findByUser(user);
    }


    public ResCartDTO convertToResCartDTO(Cart cart) {
        if (cart == null) {
            return ResCartDTO.builder().build();
        }

        ResCartDTO resCartDTO = ResCartDTO.builder().build();

        ResCartDTO.ResCartUserDTO resCartUserDTO = ResCartDTO.ResCartUserDTO
                .builder()
                .id(cart.getUser().getUserId())
                .email(cart.getUser().getEmail())
                .fullName(cart.getUser().getFullName())
                .phone(cart.getUser().getPhone())
                .build();

        List<ResCartItemDTO> resCartItemDTOs = cart.getItems()
                .stream()
                .map(this.cartItemService::convertToResCartItemDTO)
                .toList();

        double totalPrice = resCartItemDTOs.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();

        // Set information
        resCartDTO.setCartId(cart.getId());
        resCartDTO.setUserDTO(resCartUserDTO);
        resCartDTO.setItems(resCartItemDTOs);
        resCartDTO.setTotalPrice(totalPrice);

        return resCartDTO;
    }
}
