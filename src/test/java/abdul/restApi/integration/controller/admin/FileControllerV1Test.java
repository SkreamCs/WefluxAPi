package abdul.restApi.integration.controller.admin;


import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers-flyway")
public class FileControllerV1Test {
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

    @Order(1)
    @Test
    void uploadFileTestController() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("src/test/resources/files/testFile.txt"));
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/v1/files/upload/{id}").build(1))
                .header("Authorization", "Bearer" + jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus()
                .isCreated();

    }

    @Test
    void downloadFileTestController() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/files/download/{name}").build("testFile.txt"))
                .header("Authorization", "Bearer" + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getFilesTestController() {
        webTestClient.get().uri("/api/v1/files/")
                .header("Authorization", "Bearer" + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }
    @Test
    void deleteFileTestController() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/files/{id}").build(1))
                .header("Authorization", "Bearer" + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
