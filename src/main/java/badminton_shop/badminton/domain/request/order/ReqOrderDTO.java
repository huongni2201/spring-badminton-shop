package badminton_shop.badminton.domain.request.order;

import badminton_shop.badminton.utils.constant.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReqOrderDTO {
    private String fullName;
    private String phone;
    private String email;

    private String address;

    private String note;

    private double totalPrice;

    private PaymentMethod paymentMethod;

    private List<ReqOrderItemDTO> orderItems;
}
