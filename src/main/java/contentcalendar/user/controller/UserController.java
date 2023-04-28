package contentcalendar.user.controller;

import contentcalendar.user.domain.Role;
import contentcalendar.user.domain.User;
import contentcalendar.user.dto.AuthenticationResponse;
import contentcalendar.user.service.AuthenticationServiceImpl;
import contentcalendar.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/user/save")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @PostMapping("/role/save")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/role/addtouser")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addRoleToUser(@RequestBody @NonNull RoleAddToUserDto dto) {
        userService.addRoleToUser(dto.getUsername(), dto.getRoleName());
    }

    @PostMapping("/auth/register")
    @ResponseStatus(code = HttpStatus.OK)
    public AuthenticationResponse register(@NonNull @RequestBody UserAuthRequest request) {
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return authenticationService.register(user);
    }

    @PostMapping("/auth/sign-in")
    public AuthenticationResponse authenticated(@NonNull @RequestBody UserAuthRequest request) {
        return authenticationService.authenticate(request.getUsername(), request.getPassword());
    }

    @GetMapping("/auth/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refreshAccessToken(HttpServletRequest request) {
        try {
            Optional<AuthenticationResponse> response = authenticationService.refreshAccessToken(request);

            if (response.isEmpty()) throw new NoSuchElementException();
            return response.get();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Data
    private static final class RoleAddToUserDto {
        private String username;
        private String roleName;
    }

    @Data
    private static final class UserAuthRequest {
        private String name;
        private String username;
        private String password;
    }


}

