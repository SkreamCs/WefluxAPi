package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.mapper.EventMapper;
import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public Mono<Event> getByIdEvent(int id) {
        return eventRepository.findById(id);
    }

    public Flux<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Mono<Event> updateEvent(Event event) {
        event.setStatus(Status.UNDER_REVIEW);
        return eventRepository.save(event);
    }

    public Mono<Void> deleteByIdEvent(int id) {
        return eventRepository.deleteActiveById(id);
    }

    public Flux<EventDto> getEventsByUserId(int userId) {
        return eventRepository.findAllActiveByUserId(userId).map(eventMapper::map);
    }
}
