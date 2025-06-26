package badminton_shop.badminton.controller;


import badminton_shop.badminton.domain.OrderItem;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.service.OrderItemService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/order-items")
    @ApiMessage("fetch all order items")
    public ResponseEntity<ResultPaginationDTO> fetchOrderItems(
            @Filter Specification<OrderItem> spec, Pageable pageable) {
        return ResponseEntity.ok(this.orderItemService.fetchOrderItems(spec, pageable));
    }


}
