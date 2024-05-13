package abdul.restApi.spring.webflux.dto;

import lombok.ToString;

public record AuthRequestDto(
        String username,
        String password
) {

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
