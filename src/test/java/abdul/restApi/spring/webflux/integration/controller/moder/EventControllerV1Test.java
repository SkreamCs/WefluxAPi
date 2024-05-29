package abdul.restApi.spring.webflux.integration.controller.moder;


import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import abdul.restApi.spring.webflux.dto.EventDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = webTestClient.post().uri("/api/v1/auth/login")
                .bodyValue(new AuthRequestDto(
                        "AdminUserTest",
                        "Zvezda002"
                ))
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthResponseDto.class)
                .getResponseBody()
                .map(AuthResponseDto::token)
                .next()
                .block();
    }

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:latest")
            .withUsername("root")
            .withPassword("password")
            .withReuse(Boolean.FALSE);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:mysql://%s:%d/%s",
                mySQLContainer.getHost(), mySQLContainer.getFirstMappedPort(), mySQLContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", mySQLContainer::getUsername);
        registry.add("spring.r2dbc.password", mySQLContainer::getPassword);
    }

    @Order(1)
    @Test
    void updateEventTestController() {
        EventDto eventDto = new EventDto();
        eventDto.setId(2);
        eventDto.setUserId(1);
        eventDto.setFileId(2);
        webTestClient.put().uri("/api/v1/events/").header("Authorization", "Bearer " + jwtToken).bodyValue(eventDto).exchange().expectStatus().isOk();
    }

    @Test
    void getEventTestController() {
        webTestClient.get().uri("/api/v1/events/1").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isOk();
    }

    @Test
    void getAllEventsTestController() {
        webTestClient.get().uri("/api/v1/events/").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isOk();
    }

    @Test
    void getEventsByUserIdTestController() {
        webTestClient.get().uri("/api/v1/events/get-by-id/1").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isOk();
    }

    @Order(4)
    @Test
    void deleteUserTestController() {
        webTestClient.delete().uri("/api/v1/events/2").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isOk();
    }
}
