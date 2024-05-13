package abdul.restApi.spring.webflux.security;

import abdul.restApi.spring.webflux.exception.UnauthorizedException;
import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getByUsername(customPrincipal.getName())
                .filter(user -> !user.getStatus().equals(Status.DELETE))
                .switchIfEmpty(Mono.error(new UnauthorizedException("User deleted")))
                .map(user -> authentication);
    }
}