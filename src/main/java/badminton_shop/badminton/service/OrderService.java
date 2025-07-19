package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.Order;
import badminton_shop.badminton.domain.OrderItem;
import badminton_shop.badminton.domain.User;
import badminton_shop.badminton.domain.request.order.ReqOrderDTO;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.domain.response.order.ResOrderDTO;
import badminton_shop.badminton.domain.response.order.ResOrderItemDTO;
import badminton_shop.badminton.repository.OrderRepository;
import badminton_shop.badminton.utils.constant.OrderStatus;
import badminton_shop.badminton.utils.constant.PaymentMethod;
import badminton_shop.badminton.utils.exception.InvalidArgumentException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    @Value("${payment.bank.owner}")
    private String owner;

    @Value("${payment.bank.number}")
    private String bankNumber;

    @Value("${payment.bank.code}")
    private String bankCode;

    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
    }

    public ResultPaginationDTO fetchAllOrdersByUser(Specification<Order> spec, Pageable pageable, User user) {
        Page<Order> orders =getOrdersByUser(spec, pageable, user);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(orders.getTotalPages());
        meta.setTotalItems(orders.getTotalElements());

        List<ResOrderDTO> resOrders = orders.getContent().stream()
                .map(this::convertToResOrderDTO)
                .toList();

        return ResultPaginationDTO.builder()
                .meta(meta)
                .result(resOrders)
                .build();
    }

    public List<Order> fetchOrdersByUser(User user) {
        return this.orderRepository.findByUser(user);
    }

    private Page<Order> getOrdersByUser(Specification<Order> spec, Pageable pageable, User user) {
        Specification<Order> userSpec = (root, query, cb) -> cb.equal(root.get("user"), user);

        // Kết hợp với spec filter từ controller
        Specification<Order> combinedSpec = spec == null ? userSpec : userSpec.and(spec);

        return orderRepository.findAll(combinedSpec, pageable);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(ReqOrderDTO req, User currentUser) {
        Order order = Order.builder()
                .user(currentUser)
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .status(OrderStatus.PENDING)
                .address(req.getAddress())
                .note(req.getNote())
                .totalPrice(req.getTotalPrice())
                .paymentMethod(req.getPaymentMethod())
                .build();
        Order finalOrder = orderRepository.save(order);

        List<OrderItem> items = req.getOrderItems().stream()
                .map(item -> {
                    try {
                        return orderItemService.save(item, finalOrder);
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        finalOrder.setOrderItems(items);

        return orderRepository.save(finalOrder);
    }


    public ResOrderDTO convertToResOrderDTO(Order order) {

        List<ResOrderItemDTO> items = order.getOrderItems()
                .stream()
                .map(this.orderItemService::convertToResOrderItemDTO)
                .toList();

        ResOrderDTO.ResPaymentInfoDTO resPaymentInfoDTO = new ResOrderDTO.ResPaymentInfoDTO();

        if (order.getPaymentMethod() == PaymentMethod.BANK) {
            resPaymentInfoDTO.setBankName(bankCode);
            resPaymentInfoDTO.setBankOwner(owner);
            resPaymentInfoDTO.setBankNumber(bankNumber);
            resPaymentInfoDTO.setQrCodeUrl(order.getQrCodeUrl());
        }

        return ResOrderDTO.builder()
                .orderId(order.getId())
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .email(order.getEmail())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .address(order.getAddress())
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod())
                .paymentInfo(resPaymentInfoDTO)
                .items(items)
                .build();
    }

    public String buildTransferDescription(Long orderId) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "DH-" + date + "-" + orderId;
    }


}
