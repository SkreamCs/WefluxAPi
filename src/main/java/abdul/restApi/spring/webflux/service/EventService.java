package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.mapper.EventMapper;
import abdul.restApi.spring.webflux.mapper.FileMapper;
import abdul.restApi.spring.webflux.mapper.UserMapper;
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
    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public Mono<Event> getByIdEvent(int id) {
        return eventRepository.findById(id);
    }

    public Flux<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Mono<Event> createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Mono<Event> updateEvent(Event event) {
        event.setStatus(Status.UNDER_REVIEW);
        return eventRepository.save(event);
    }

    public Mono<Void> deleteByIdEvent(int id) {
        return eventRepository.deleteActiveById(id);
    }

    public Flux<EventDto> getEventsByUserId(int userId) {
        return eventRepository.findAllActiveByUserId(userId).map(event -> {
            userMapper.map(event.getUser());
            fileMapper.map(event.getFile());
            return eventMapper.map(event);
        });
    }
}
