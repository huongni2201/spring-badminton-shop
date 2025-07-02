package badminton_shop.badminton.service;

import badminton_shop.badminton.domain.OrderItem;
import badminton_shop.badminton.domain.response.ResultPaginationDTO;
import badminton_shop.badminton.repository.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

}
