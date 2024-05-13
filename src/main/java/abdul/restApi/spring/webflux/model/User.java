package abdul.restApi.spring.webflux.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "users")
public class User {
    @Id
    @Column(value = "id")
    private int id;
    @Column(value = "username")
    private String username;
    @Column(value = "email")
    private String email;
    @Column(value = "password")
    private String password;
    @Transient
    @JsonIgnore
    private List<Event> eventList;
    @Column(value = "role")
    private Role role;
    @CreatedDate
    @Column(value = "created")
    private LocalDateTime created;
    @LastModifiedDate
    @Column(value = "updated")
    private LocalDateTime updated;

    @Column(value = "status")
    private Status status;
}
