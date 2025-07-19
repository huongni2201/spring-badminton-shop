package badminton_shop.badminton.utils.exception;

import badminton_shop.badminton.domain.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class,
            MissingRequestCookieException.class
    })
    public ResponseEntity<RestResponse<Object>> handleAuthenticationExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(400, "Yêu cầu không hợp lệ", ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RestResponse.fail(404, "Không tìm thấy tài nguyên", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        List<String> messages = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        String message = messages.size() > 1 ? "Nhiều lỗi xác thực xảy ra" : messages.get(0);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(400, message, String.join(", ", messages)));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleFileUploadError(StorageException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(400, "Lỗi khi tải tệp lên", ex.getMessage()));
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<RestResponse<Object>> handleInvalidArgument(InvalidArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.fail(400, "Tham số không hợp lệ", ex.getMessage()));
    }

    // CATCH-ALL: Bắt các lỗi không khai báo riêng
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestResponse.internalError("Lỗi không xác định: " + ex.getMessage()));
    }
}
