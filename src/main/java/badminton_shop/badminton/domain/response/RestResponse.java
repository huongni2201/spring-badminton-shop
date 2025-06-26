package badminton_shop.badminton.domain.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
