package badminton_shop.badminton.utils.error;


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

    @ExceptionHandler(value =
            {UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class,
            MissingRequestCookieException.class})
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> resResponse = new RestResponse<>();
        resResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        resResponse.setMessage("Exception occurs...");
        resResponse.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(resResponse);
    }

    @ExceptionHandler(value =
            {NoResourceFoundException.class})
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> resResponse = new RestResponse<>();
        resResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        resResponse.setMessage("404 Not Found...");
        resResponse.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(resResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();

        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).toList();
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}