package badminton_shop.badminton.controller;


import badminton_shop.badminton.domain.Order;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.order.ReqOrderDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.domain.response.order.ResOrderDTO;
import badminton_shop.badminton.service.OrderService;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> fetchOrders(
            @Filter Specification<Order> spec, Pageable pageable
            ) throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();

        return ResponseEntity.ok(this.orderService.fetchAllOrdersByUser(spec, pageable, currentUser));
    }

    @PostMapping("")
    public ResponseEntity<ResOrderDTO> createOrder(@RequestBody ReqOrderDTO req) throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();

        Order order = this.orderService.createOrder(req, currentUser);

        return ResponseEntity.ok(this.orderService.convertToResOrderDTO(order));
    }
}
