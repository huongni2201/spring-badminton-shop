package badminton_shop.badminton.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service
public class PaymentService {

    private static final String QR_CODE_TEMPLATE = "https://qr.sepay.vn/img";

    @Value("${payment.bank.number}")
    private String accountNumber;

    @Value("${payment.bank.code}")
    private String bankCode;

    @Value("${payment.bank.template:compact}")
    private String template;

    public String generateQrCode(double amount, String description) {
        String encodeDescription = URLEncoder.encode(description);

        return String.format("%s?acc=%s&bank=%s&amount=%s&des=%s&template=%s&download=DOWNLOAD",
                QR_CODE_TEMPLATE,
                accountNumber,
                bankCode,
                amount,
                encodeDescription,
                template);
    }
}
