package abdul.restApi.spring.webflux.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class EventDto {
    private int id;
    private int userId;
    private int fileId;
}
