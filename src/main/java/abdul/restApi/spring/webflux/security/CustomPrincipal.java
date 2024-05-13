package abdul.restApi.spring.webflux.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {
    private String name;
}
