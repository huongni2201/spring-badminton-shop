package badminton_shop.badminton.domain.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private String message;
    private T data;

    // ===== Static factory methods (mở rộng dễ) =====

    public static <T> RestResponse<T> success(T data, String message) {
        return RestResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> RestResponse<T> success(T data) {
        return success(data, "Thành công");
    }

    public static <T> RestResponse<T> fail(int statusCode, String message, String error) {
        return RestResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .build();
    }

    public static <T> RestResponse<T> badRequest(String message) {
        return fail(400, message, "Bad Request");
    }

    public static <T> RestResponse<T> unauthorized(String message) {
        return fail(401, message, "Unauthorized");
    }

    public static <T> RestResponse<T> forbidden(String message) {
        return fail(403, message, "Forbidden");
    }

    public static <T> RestResponse<T> notFound(String message) {
        return fail(404, message, "Not Found");
    }

    public static <T> RestResponse<T> internalError(String message) {
        return fail(500, message, "Internal Server Error");
    }
}
