package badminton_shop.badminton.controller;

import badminton_shop.badminton.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {
    private final OrderService orderService;

    public PaymentWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

//    @PostMapping("/webhook/sepay")
//    public ResponseEntity<?> handleSepayWebhook(@RequestBody String body) {}
}
