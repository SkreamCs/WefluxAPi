package abdul.restApi.spring.webflux.rest;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.mapper.EventMapper;
import abdul.restApi.spring.webflux.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventRestControllerV1 {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Mono<EventDto> getEventsById(@PathVariable String id) {
        return eventService.getByIdEvent(Integer.parseInt(id)).map(eventMapper::map);
    }

    @PutMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<EventDto> updateEvents(@RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventMapper.map(eventDto)).map(eventMapper::map);
    }


    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Flux<EventDto> getAllEvents() {
        return eventService.getAllEvents().map(eventMapper::map);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<?>> deleteEvents(@PathVariable int id) {
        return eventService.deleteByIdEvent(id).thenReturn(ResponseEntity.ok("successful removal"));
    }

    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Flux<EventDto> getAllEventsByUserId(@PathVariable int id) {
        return eventService.getEventsByUserId(id);
    }
}
