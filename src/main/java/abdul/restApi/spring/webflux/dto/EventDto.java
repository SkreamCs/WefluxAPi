package abdul.restApi.spring.webflux.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private int id;
    private UserDto user;
    private FileDto file;
}
