package abdul.restApi.spring.webflux.integration.controller.user;


import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-containers")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerV1Test {


    @Autowired
    private WebTestClient webTestClient;

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

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = webTestClient.post().uri("/api/v1/auth/login")
                .bodyValue(new AuthRequestDto(
                        "UserUserTest",
                        "Zvezda002"
                ))
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthResponseDto.class)
                .getResponseBody()
                .map(AuthResponseDto::token)
                .next().block();
    }

    @Order(1)
    @Test
    void saveUserTestController() {
        String json = "{\"username\":\"Zvezda002\", \"password\":\"Zvezda002\", \"role\": \"ADMIN\", \"email\":\"emailTest@gmail.com\"}";
        webTestClient.post().uri("/api/v1/users/").header("Authorization", "Bearer " + jwtToken).contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange().expectStatus().isForbidden();

    }

    @Order(2)
    @Test
    void updateUserTestController() {
        String json = "{\"id\":\"2\", \"username\":\"Skream\", \"password\":\"Zvezda002\", \"role\":\"ADMIN\", \"email\":\"emailTest3@gmail.com\", \"created\":\"2024-05-28T10:36:19\", \"updated\":\"2024-05-28T10:36:19\", \"status\":\"ACTIVE\"}";
        webTestClient.put().uri("/api/v1/users/").header("Authorization", "Bearer " + jwtToken).contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange().expectStatus().isForbidden();
    }

    @Test
    void getUserTestController() {
        webTestClient.get().uri("/api/v1/users/1").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isOk();
    }

    @Test
    void getAllUserTestController() {
        webTestClient.get().uri("/api/v1/users/").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isForbidden();
    }

    @Order(3)
    @Test
    void deleteUserTestController() {
        webTestClient.delete().uri("/api/v1/users/2").header("Authorization", "Bearer " + jwtToken).exchange().expectStatus().isForbidden();
    }
}

