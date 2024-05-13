package abdul.restApi.spring.webflux.repository;

import abdul.restApi.spring.webflux.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {
    @Query("select * from users where status = 'ACTIVE' and username = :username")
    Mono<User> findByUsername(String username);

    @Query("update users u set status = 'DELETED' where status = 'ACTIVE' and u.id = :id")
    Mono<Void> deleteActiveById(Integer id);
}
