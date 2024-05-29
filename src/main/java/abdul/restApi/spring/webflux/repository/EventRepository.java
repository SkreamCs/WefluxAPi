package abdul.restApi.spring.webflux.repository;

import abdul.restApi.spring.webflux.model.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository

public interface EventRepository extends R2dbcRepository<Event, Integer> {
    @Query("select e.*, u.*, f.* from events e LEFT JOIN users u ON e.user_id = u.id LEFT JOIN files f ON e.file_id = f.id WHERE e.id = :eventId")
    Mono<Event> findById(int eventId);

    @Query("update events e set status = 'DELETED' where status = 'ACTIVE' and e.id = :id")
    Mono<Void> deleteActiveById(int id);

    @Query("select * from events where status = 'ACTIVE' and user_id = :userId")
    Flux<Event> findAllActiveByUserId(int userId);

}
