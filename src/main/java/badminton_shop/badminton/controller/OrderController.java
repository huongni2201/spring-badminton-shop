package badminton_shop.badminton.controller;


import badminton_shop.badminton.domain.Order;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.order.ReqOrderDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.domain.response.order.ResOrderDTO;
import badminton_shop.badminton.service.MailService;
import badminton_shop.badminton.service.OrderService;
import badminton_shop.badminton.service.PaymentService;
import badminton_shop.badminton.service.UserService;
import badminton_shop.badminton.utils.constant.EmailType;
import badminton_shop.badminton.utils.constant.PaymentMethod;
import badminton_shop.badminton.utils.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final MailService mailService;
    private final PaymentService paymentService;

    public OrderController(
            OrderService orderService,
            UserService userService,
            MailService mailService,
            PaymentService paymentService
    ) {
        this.orderService = orderService;
        this.userService = userService;
        this.mailService = mailService;
        this.paymentService = paymentService;
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> fetchOrders(
            @Filter Specification<Order> spec,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();

        return ResponseEntity.ok(this.orderService.fetchAllOrdersByUser(spec, pageable, currentUser));
    }

    @PostMapping("")
    public ResponseEntity<ResOrderDTO> createOrder(@RequestBody ReqOrderDTO req) throws IdInvalidException {
        User currentUser = this.userService.fetchUserLogin();

        Order order = this.orderService.createOrder(req, currentUser);

        if (order != null && order.getPaymentMethod() == PaymentMethod.BANK) {
            String desc = this.orderService.buildTransferDescription(order.getId());
            String qrCodeUrl = this.paymentService.generateQrCode(req.getTotalPrice(), desc);
            order.setQrCodeUrl(qrCodeUrl);
            this.orderService.save(order);
        }

        if (order != null) {
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("customerName", order.getFullName());
                variables.put("orderId", order.getId());
                variables.put("totalPrice", order.getTotalPrice());
                variables.put("address", order.getAddress());

                mailService.sendEmail(order.getEmail(), EmailType.ORDER_CONFIRMATION, variables);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(this.orderService.convertToResOrderDTO(order));
    }
}
