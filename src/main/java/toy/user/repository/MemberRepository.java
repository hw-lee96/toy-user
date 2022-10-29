package toy.user.repository;

import org.springframework.data.repository.CrudRepository;

import toy.user.model.Member;

public interface MemberRepository extends CrudRepository<Member, Long>{
    Member findByUsername(String username);
}