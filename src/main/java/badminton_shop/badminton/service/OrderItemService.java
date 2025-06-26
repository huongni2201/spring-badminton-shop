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

    public ResultPaginationDTO fetchOrderItems(Specification<OrderItem> spec, Pageable pageable) {
        Page<OrderItem> orderItems = this.orderItemRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(orderItems.getTotalPages());
        meta.setTotalItems(orderItems.getTotalElements());

        res.setMeta(meta);
        res.setResult(orderItems.getContent());

        return res;
    }
}
