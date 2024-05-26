package abdul.restApi.integration.controller.admin;


import abdul.restApi.spring.webflux.dto.*;
import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers-flyway")
public class EventControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = webTestClient.post().uri("/api/v1/auth/login")
                .bodyValue(new AuthRequestDto(
                        "TestUserRoleUser",
                        "password"
                ))
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthResponseDto.class)
                .getResponseBody()
                .map(AuthResponseDto::token)
                .next().block();
    }
    @Order(2)
    @Test
    void updateEventTestController() {
        EventDto eventDto = new EventDto();
        eventDto.setFile(FileDto.builder().id(1).build());
        eventDto.setUser(UserDto.builder().id(1).build());
        webTestClient.put().uri("/api/v1/events/").header("Authorization",  "Bearer" + jwtToken).bodyValue(eventDto).exchange().expectStatus().isOk();
    }
    @Test
    void getEventTestController() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/events/{id}").build(1)).header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
    @Test
    void getAllEventsTestController() {
        webTestClient.get().uri("/api/v1/events/").header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
    @Test
    void getEventsByUserIdTestController() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/events/{by-user-id}").build(1)).header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
    @Order(3)
    @Test
    void deleteUserTestController() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/events/{id}").build(3)).header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
}
