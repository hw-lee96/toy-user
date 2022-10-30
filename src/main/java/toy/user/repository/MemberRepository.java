package toy.user.repository;

import org.springframework.data.repository.CrudRepository;
import toy.user.entity.User;

public interface MemberRepository extends CrudRepository<User, Long>{
    User findByUsername(String username);
}