package contentcalendar.user.repo;

import contentcalendar.user.domain.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends MongoRepository<Token,String> {
   @Query("{isExpired: false, isRevoked: false}")
   List<Token> findAllValidTokenByUserId(String id);

   Optional<Token> findByToken(String token);
}
