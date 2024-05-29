package abdul.restApi.spring.webflux.dto;

import lombok.Builder;
import lombok.ToString;

@Builder(toBuilder = true)
public record AuthRequestDto(
        String username,
        String password
) {

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
