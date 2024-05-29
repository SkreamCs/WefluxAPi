package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.mapper.EventMapper;
import abdul.restApi.spring.webflux.mapper.FileMapper;
import abdul.restApi.spring.webflux.mapper.UserMapper;
import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EventMapper eventMapper;
    @Mock
    FileMapper fileMapper;

    @InjectMocks
    private EventService eventServiceTest;

    private List<Event> getEvents() {
        return List.of(
                Event.builder().id(1).fileId(1).userId(1).status(Status.ACTIVE).build(),
                Event.builder().id(2).fileId(1).userId(2).status(Status.ACTIVE).build(),
                Event.builder().id(3).fileId(1).userId(3).status(Status.ACTIVE).build(),
                Event.builder().id(4).fileId(1).userId(4).status(Status.ACTIVE).build()
        );
    }

    private Event getEvent() {
        return Event.builder().id(1).fileId(1).userId(1).status(Status.ACTIVE).build();
    }

    @Test
    void getEventByIdTest() {
        int eventId = 1;
        when(eventRepository.findById(anyInt())).thenReturn(Mono.just(getEvent()));
        Mono<Event> expectedEvent = eventServiceTest.getByIdEvent(eventId);
        StepVerifier.create(expectedEvent)
                .expectNext(getEvent())
                .verifyComplete();
    }

    @Test
    void getAllUserTest() {
        when(eventRepository.findAll()).thenReturn(Flux.fromIterable(getEvents()));
        Flux<Event> events = eventServiceTest.getAllEvents();
        StepVerifier.create(events)
                .expectNext(getEvents().get(0))
                .expectNext(getEvents().get(1))
                .expectNext(getEvents().get(2))
                .expectNext(getEvents().get(3))
                .verifyComplete();
    }

    @Test
    void update() {
        Event expectedEvent = getEvent().toBuilder().status(Status.UNDER_REVIEW).build();
        when(eventRepository.save(expectedEvent)).thenReturn(Mono.just(expectedEvent));
        Mono<Event> event = eventServiceTest.updateEvent(getEvent());
        StepVerifier.create(event)
                .expectNext(expectedEvent)
                .verifyComplete();

    }

    @Test
    void deleteById() {
        int deleteId = 1;
        when(eventRepository.deleteActiveById(anyInt())).thenReturn(Mono.empty());
        eventServiceTest.deleteByIdEvent(deleteId);
        verify(eventRepository).deleteActiveById(eq(deleteId));
    }

    @Test
    void getAllEventsByUserIdTest() {
        int userId = 4;
        List<EventDto> expectedDto = List.of(
                EventDto.builder().id(1).fileId(1).userId(1).build(),
                EventDto.builder().id(2).fileId(1).userId(2).build());

        when(eventRepository.findAllActiveByUserId(anyInt())).thenReturn(Flux.fromIterable(List.of(getEvents().get(0), getEvents().get(1))));
        when(eventMapper.map(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            return expectedDto.stream().filter(dto -> dto.getId() == event.getId()).findFirst().orElse(null);
        });

        Flux<EventDto> eventsDto = eventServiceTest.getEventsByUserId(userId);
        StepVerifier.create(eventsDto)
                .expectNext(expectedDto.get(0))
                .expectNext(expectedDto.get(1))
                .verifyComplete();
    }
}
