package school.hei.demo.endpoint.rest.exception;

import org.springframework.http.HttpStatus;
import school.hei.demo.endpoint.rest.exception.ApiException;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}