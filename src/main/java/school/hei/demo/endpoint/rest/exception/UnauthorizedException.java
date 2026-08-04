package school.hei.demo.endpoint.rest.exception;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message){
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
