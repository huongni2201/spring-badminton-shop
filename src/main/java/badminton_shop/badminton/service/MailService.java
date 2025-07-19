package badminton_shop.badminton.service;

import badminton_shop.badminton.utils.constant.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;


@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, EmailType type, Map<String, Object> variables) throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(type.getTemplateName(), context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(type.getDefaultSubject());
        helper.setText(html, true);

        mailSender.send(message);
    }

}
