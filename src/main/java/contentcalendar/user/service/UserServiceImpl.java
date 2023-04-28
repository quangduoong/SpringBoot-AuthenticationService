package contentcalendar.user.service;

import contentcalendar.user.domain.Role;
import contentcalendar.user.domain.Token;
import contentcalendar.user.domain.User;
import contentcalendar.user.repo.RoleRepo;
import contentcalendar.user.repo.TokenRepo;
import contentcalendar.user.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final TokenRepo tokenRepo;

    @Override
    public void logout(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
             Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String jwt;
        Token token;

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;
        jwt = authHeader.substring(7);
        token = tokenRepo.findByToken(jwt).orElse(null);
        if (token != null) {
//            token.setExpired(true);
//            token.setRevoked(true);
            tokenRepo.delete(token);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user == null) {
            String errorMessage = String.format("User %s not found", username);
            log.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        log.info("User " + username + " found.");
        user.getRoles().forEach(
                role -> authorities
                        .add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    @Override
    public void saveUser(User user) {
        log.info("Saving new user {} to db...", user.getName());
        userRepo.save(user);
    }

    @Override
    public void saveRole(Role role) {
        log.info("Saving new role {} to db...", role.getName());
        roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}...", roleName, username);
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("Fetching user {}...", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("Getting all users...");
        return userRepo.findAll();
    }


}
