package badminton_shop.badminton.utils.constant;

public enum EmailType {
    ORDER_CONFIRMATION("email-order", "Xác nhận đơn hàng"),
    REGISTER("email-register", "Chào mừng bạn!"),
    RESET_PASSWORD("email-reset-password", "Đặt lại mật khẩu");

    private final String templateName;
    private final String defaultSubject;

    EmailType(String templateName, String defaultSubject) {
        this.templateName = templateName;
        this.defaultSubject = defaultSubject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }
}
