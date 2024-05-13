package abdul.restApi.spring.webflux.repository;

import abdul.restApi.spring.webflux.model.File;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FileRepository extends R2dbcRepository<File, Integer> {
    @Query("update files f set status = 'DELETED' where status = 'ACTIVE' and f.file_name = :fileName")
    Mono<Void> deleteActiveByFileName(String fileName);

}
