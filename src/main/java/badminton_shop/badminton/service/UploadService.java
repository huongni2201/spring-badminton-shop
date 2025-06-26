package badminton_shop.badminton.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import jakarta.servlet.ServletContext;

@Service
public class UploadService {
    private static final Pattern DIACRITIC_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9._-]");

    private final ServletContext servletContext;

    public UploadService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void handleSaveUploadFile() {

    }

    public static String generateSafeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty())
            return "";

        String extension = "";
        String namePart = originalFileName;

        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalFileName.substring(dotIndex);
            namePart = originalFileName.substring(0, dotIndex);
        }

        // Normalize, remove diacritics, convert đ/Đ, replace special chars
        String normalized = Normalizer.normalize(namePart, Form.NFD);
        String noDiacritics = DIACRITIC_PATTERN.matcher(normalized).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'D');

        String cleanName = SPECIAL_CHAR_PATTERN.matcher(noDiacritics).replaceAll("_");

        return UUID.randomUUID() + "_" + cleanName + extension;
    }
}
