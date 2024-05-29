package abdul.restApi.spring.webflux.integration.controller.moder;


import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = webTestClient.post().uri("/api/v1/auth/login")
                .bodyValue(new AuthRequestDto(
                        "ModeratorUserTest",
                        "Zvezda002"
                ))
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthResponseDto.class)
                .getResponseBody()
                .map(AuthResponseDto::token)
                .next().block();
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
    void uploadFileTestController() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("/files/testFile.txt"));
        webTestClient.post().uri("/api/v1/files/1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus()
                .isCreated();

    }

    @Test
    void downloadFileTestController() {
        webTestClient.get().uri("/api/v1/files/download/testFile.txt")
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getFilesTestController() {
        webTestClient.get().uri("/api/v1/files/")
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @Order(4)
    void deleteFileTestController() {
        webTestClient.delete().uri("/api/v1/files/1")
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
