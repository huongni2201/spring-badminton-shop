package badminton_shop.badminton.utils;

import java.util.Arrays;

public class MyUtils {

    public static String generateSKU(String name, String color, String size) {
        // nameCode: lấy chữ cái đầu mỗi từ trong name
        String nameCode = "PROD";
        if (name != null && !name.trim().isEmpty()) {
            nameCode = Arrays.stream(name.trim().split("\\s+"))
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.substring(0, 1).toUpperCase())
                    .reduce("", String::concat);
        }

        // colorCode: lấy full chữ cái, viết hoa
        String colorCode = "COLOR";
        if (color != null && !color.trim().isEmpty()) {
            colorCode = color.replaceAll("[^A-Za-zÀ-Ỷà-ỷ]", "").toUpperCase();
        }

        // sizeCode: viết hoa toàn bộ
        String sizeCode = (size != null && !size.trim().isEmpty()) ? size.toUpperCase() : "SIZE";

        // timestamp
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmm"));

        return String.format("%s-%s-%s-%s", nameCode, colorCode, sizeCode, timestamp);
    }


}
