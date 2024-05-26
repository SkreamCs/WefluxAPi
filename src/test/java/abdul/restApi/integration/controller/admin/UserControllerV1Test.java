package abdul.restApi.integration.controller.admin;


import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import abdul.restApi.spring.webflux.dto.UserDto;
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
public class UserControllerV1Test {
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
    void saveUserTestController() {
        UserDto userDto = UserDto.builder().email("abdul06@gmail.com").username("username").password("TestPassword").role(Role.USER.name()).build();
        webTestClient.post().uri("/api/v1/users/").header("Authorization",  "Bearer" + jwtToken).bodyValue(userDto).exchange().expectStatus().isCreated();

    }
    @Order(2)
    @Test
    void updateUserTestController() {
        UserDto userDto = UserDto.builder().id(3).email("updateEmail02@gmail.com").username("username").password("TestPassword").role(Role.USER.name()).build();
        webTestClient.put().uri("/api/v1/users/").header("Authorization",  "Bearer" + jwtToken).bodyValue(userDto).exchange().expectStatus().isOk();
    }
    @Test
    void getUserTestController() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/users/{id}").build(1)).header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
    @Test
    void getAllUserTestController() {
        webTestClient.get().uri("/api/v1/users/").header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
    @Order(3)
    @Test
    void deleteUserTestController() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/users/{id}").build(3)).header("Authorization",  "Bearer" + jwtToken).exchange().expectStatus().isOk();
    }
}
