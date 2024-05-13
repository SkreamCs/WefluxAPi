package abdul.restApi.service;

import abdul.restApi.spring.webflux.model.Role;
import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.model.User;
import abdul.restApi.spring.webflux.repository.UserRepository;
import abdul.restApi.spring.webflux.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userServiceTest;

    private List<User> getUsers() {
        return List.of(
                new User(1, "user", "email_1", "pass_1", new ArrayList<>(), Role.USER, null, null, Status.ACTIVE),
                new User(2, "user", "email_2", "pass_2", new ArrayList<>(), Role.USER, null, null, Status.ACTIVE),
                new User(3, "moderator", "email_3", "pass_3", new ArrayList<>(), Role.MODERATOR, null, null, Status.ACTIVE),
                new User(4, "admin", "email_4", "pass_4", new ArrayList<>(), Role.ADMIN, null, null, Status.ACTIVE)

        );
    }

    private User getUser() {
        return new User(4, "admin", "email_4", "pass_4", new ArrayList<>(), Role.ADMIN, null, null, Status.ACTIVE);
    }


    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Mono.just(getUser()));
        Mono<User> user = userServiceTest.getUserById(4);
        StepVerifier.create(user)
                .expectNext(getUser())
                .verifyComplete();
    }

    @Test
    void getAllUserTest() {
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(getUsers()));
        Flux<User> user = userServiceTest.getAllUser();
        StepVerifier.create(user)
                .expectNext(getUsers().get(0))
                .expectNext(getUsers().get(1))
                .expectNext(getUsers().get(2))
                .expectNext(getUsers().get(3))
                .verifyComplete();
    }

    @Test
    void findByUsernameTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(getUser()));
        Mono<User> user = userServiceTest.getByUsername("name_1");
        StepVerifier.create(user)
                .expectNext(getUser())
                .verifyComplete();
    }

    @Test
    void createUserTest() {
        User saveUser = new User(0, "admin", "email_4", "pass_4", new ArrayList<>(), Role.ADMIN, null, null, Status.ACTIVE);
        ;
        String rawPassword = "pass_4";
        when(passwordEncoder.encode(anyString())).thenReturn(rawPassword);
        when(userRepository.save(saveUser)).thenReturn(Mono.just(getUser()));

        Mono<User> user = userServiceTest.create(saveUser);
        StepVerifier.create(user)
                .expectNext(getUser())
                .verifyComplete();
    }


    @Test
    void updateUserTest() {
        User expectedUser = getUser().toBuilder().status(Status.UNDER_REVIEW).build();
        when(userRepository.save(expectedUser)).thenReturn(Mono.just(expectedUser));
        Mono<User> user = userServiceTest.update(getUser());
        StepVerifier.create(user)
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    void deleteUserByIdTest() {
        int deleteId = 4;
        when(userRepository.deleteActiveById(anyInt())).thenReturn(Mono.empty());
        userServiceTest.deleteUser(deleteId);
        verify(userRepository).deleteActiveById(eq(deleteId));
    }
}
