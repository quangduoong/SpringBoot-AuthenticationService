package contentcalendar.user.repo;

import contentcalendar.user.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepo extends MongoRepository<Role, String> {
    Role findByName(String name);
}
