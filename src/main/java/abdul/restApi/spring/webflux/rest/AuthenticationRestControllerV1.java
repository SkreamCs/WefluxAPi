package abdul.restApi.spring.webflux.rest;

import abdul.restApi.spring.webflux.dto.AuthRequestDto;
import abdul.restApi.spring.webflux.dto.AuthResponseDto;
import abdul.restApi.spring.webflux.dto.UserDto;
import abdul.restApi.spring.webflux.mapper.UserMapper;
import abdul.restApi.spring.webflux.model.User;
import abdul.restApi.spring.webflux.security.SecurityService;
import abdul.restApi.spring.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        User user = userMapper.map(dto);
        return userService.create(user)
                .map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return securityService.authenticate(dto.username(), dto.password())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .token(tokenDetails.getToken())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }
}


