package abdul.restApi.service;

import abdul.restApi.spring.webflux.dto.EventDto;
import abdul.restApi.spring.webflux.dto.FileDto;
import abdul.restApi.spring.webflux.dto.UserDto;
import abdul.restApi.spring.webflux.mapper.EventMapper;
import abdul.restApi.spring.webflux.mapper.FileMapper;
import abdul.restApi.spring.webflux.mapper.UserMapper;
import abdul.restApi.spring.webflux.model.*;
import abdul.restApi.spring.webflux.repository.EventRepository;
import abdul.restApi.spring.webflux.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;


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
                new Event(1, new User(4, "admin", "email_4", "pass_4", new ArrayList<>(), Role.ADMIN, null, null, Status.ACTIVE), new File(), Status.ACTIVE),
                new Event(2, new User(4, "admin", "email_4", "pass_4", new ArrayList<>(), Role.ADMIN, null, null, Status.ACTIVE), new File(), Status.ACTIVE),
                new Event(3, new User(), new File(), Status.ACTIVE),
                new Event(4, new User(), new File(), Status.ACTIVE)
        );
    }

    private Event getEvent() {
        return new Event(1, new User(), new File(), Status.ACTIVE);
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
    void createEventTest() {
        Event event = new Event(0, new User(), new File(), Status.ACTIVE);
        when(eventRepository.save(event)).thenReturn(Mono.just(getEvent()));
        Mono<Event> expectedEvent = eventServiceTest.createEvent(event);
        StepVerifier.create(expectedEvent)
                .expectNext(getEvent())
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
                new EventDto(1, new UserDto(4, "admin", "email_4", "pass_4", Role.ADMIN.name(), null, null), new FileDto()),
                new EventDto(2, new UserDto(4, "admin", "email_4", "pass_4", Role.ADMIN.name(), null, null), new FileDto()));

        when(eventRepository.findAllActiveByUserId(anyInt())).thenReturn(Flux.fromIterable(List.of(getEvents().get(0), getEvents().get(1))));
        when(userMapper.map(any(User.class))).thenReturn(new UserDto(4, "admin", "email_4", "pass_4", Role.ADMIN.name(), null, null));
        when(fileMapper.map(any(File.class))).thenReturn(new FileDto());
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
