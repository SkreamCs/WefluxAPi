package abdul.restApi.spring.webflux.dto;

import lombok.Builder;

import java.util.Date;

@Builder(toBuilder = true)
public record AuthResponseDto(
        String token,
        Date expiresAt
) {

}
