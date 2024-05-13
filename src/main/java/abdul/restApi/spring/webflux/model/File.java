package abdul.restApi.spring.webflux.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("files")
@Builder(toBuilder = true)
public class File {
    @Id
    @Column(value = "id")
    private int id;
    @Column(value = "location")
    private String location;
    @Column(value = "file_name")
    private String fileName;
    @Column(value = "status")
    private Status status;
}
