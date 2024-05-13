package abdul.restApi.spring.webflux.exception;


public class ApiException extends RuntimeException {


    public ApiException(String message) {
        super(message);

    }
}