package contentcalendar.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import contentcalendar.user.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
