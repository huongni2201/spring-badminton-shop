package badminton_shop.badminton.domain.response.order;

import badminton_shop.badminton.utils.constant.OrderStatus;
import badminton_shop.badminton.utils.constant.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class ResOrderDTO {
    private Long orderId;
    private OrderStatus orderStatus;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    private double totalPrice;

    private String note;
    private PaymentMethod paymentMethod;

    private ResPaymentInfoDTO paymentInfo;

    private List<ResOrderItemDTO> items;

    private Instant createdAt;

    @Getter
    @Setter
    public static class ResPaymentInfoDTO {
        private String bankOwner;
        private String bankNumber;
        private String bankName;
        private String qrCodeUrl;


    }

}
