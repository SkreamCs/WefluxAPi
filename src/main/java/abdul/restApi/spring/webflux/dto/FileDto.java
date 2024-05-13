package abdul.restApi.spring.webflux.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDto {
    private int id;
    private String location;
    private String filename;
}

