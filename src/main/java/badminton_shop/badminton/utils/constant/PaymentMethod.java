package badminton_shop.badminton.utils.constant;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    BANK("Chuyển khoản ngân hàng"),
    MOMO("Thanh toán Momo");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
