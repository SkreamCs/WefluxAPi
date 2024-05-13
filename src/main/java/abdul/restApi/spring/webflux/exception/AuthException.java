package abdul.restApi.spring.webflux.exception;

public class AuthException extends ApiException {
    public AuthException(String message) {
        super(message);
    }
}