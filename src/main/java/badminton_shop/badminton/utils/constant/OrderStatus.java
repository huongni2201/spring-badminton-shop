package badminton_shop.badminton.utils.constant;

public enum OrderStatus {
    PENDING("Đang chờ xác nhận"),
    PAID("Đã thanh toán"),
    SHIPPED("Đã giao hàng"),
    DELIVERED("Đã nhận hàng"),
    CANCELLED("Đã hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
