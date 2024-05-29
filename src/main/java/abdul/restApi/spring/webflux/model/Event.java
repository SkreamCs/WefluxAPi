package abdul.restApi.spring.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
@Builder(toBuilder = true)
public class Event {
    @Id
    @Column(value = "id")
    private int id;

    private int userId;

    private int fileId;

    @Transient
    private User user;
    @Transient
    private File file;

    @Column(value = "status")
    private Status status;
}
