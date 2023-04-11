package contentcalendar.user.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import contentcalendar.user.service.AuthenticationServiceImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import contentcalendar.user.domain.Role;
import contentcalendar.user.domain.User;
import contentcalendar.user.service.UserService;

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
    public String register(@NonNull @RequestBody UserAuthRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setName("gi cung dc");
        user.setId(UUID.randomUUID());
        return authenticationService.register(user);
    }

    @PostMapping ("/auth/sign-in")
    public String authenticated(@NonNull @RequestBody UserAuthRequest request){
        return authenticationService.authenticate(request.getUsername(), request.getPassword());
    }

    @Data
    private static final class RoleAddToUserDto {
        private String username;
        private String roleName;
    }

    @Data
    private static final class UserAuthRequest {
        private String username;
       private String password;
    }


}

