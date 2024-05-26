package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.model.User;
import abdul.restApi.spring.webflux.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public Mono<Void> deleteUser(int userId) {
        return userRepository.deleteActiveById(userId);
    }

    public Mono<User> update(User user) {
        user.setStatus(Status.UNDER_REVIEW);
        return userRepository.save(user);
    }

    public Mono<User> create(User user) {
        return userRepository.save(user.toBuilder()
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .role(user.getRole())
                .username(user.getUsername())
                .build());
    }

    public Flux<User> getAllUser() {
        return userRepository.findAll();
    }

    public Mono<User> getByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }
}
