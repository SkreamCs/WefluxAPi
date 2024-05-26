package abdul.restApi.spring.webflux.rest;

import abdul.restApi.spring.webflux.dto.UserDto;
import abdul.restApi.spring.webflux.mapper.UserMapper;
import abdul.restApi.spring.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Mono<UserDto> getUserById(@PathVariable int id) {
        return userService.getUserById(id).map(userMapper::map);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Flux<UserDto> getAll() {
        return userService.getAllUser().map(userMapper::map);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> save(@RequestBody UserDto userDto) {
        return userService.create(userMapper.map(userDto)).map(userMapper::map);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> update(@RequestBody UserDto userDto) {
        return userService.update(userMapper.map(userDto)).map(userMapper::map);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<?>> delete(@PathVariable int id) {
       return userService.deleteUser(id).thenReturn(ResponseEntity.ok("successful removal"));

    }
}
