package abdul.restApi.spring.webflux.dto;


import abdul.restApi.spring.webflux.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDto {
    private int id;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String role;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Status status;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}

