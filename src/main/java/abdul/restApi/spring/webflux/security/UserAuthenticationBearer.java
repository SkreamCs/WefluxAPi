package abdul.restApi.spring.webflux.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

public class UserAuthenticationBearer {

    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult) {
        Claims claims = verificationResult.claims;
        String subject = claims.getSubject();

        String role = claims.get("role", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(new CustomPrincipal(subject), null, authorities));
    }
}
