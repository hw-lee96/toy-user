package toy.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.user.entity.User;

import java.util.Optional;

// JpaRepository 를 extends 하여 findAll, create 등 기본적인 메소드를 사용할 수 있게 함
public interface UserRepository extends JpaRepository<User, Long> {
    /*
    * user + authorities 정보를 함께 조회
    * EntityGraph는 기본이 eager(즉시로딩) 조회로 연관관계에 있는 내용을 전부 가져온다.
    * 반대로 연관관계는 알빠 아니고 요청한것만 조회하는 lazy(지연로딩) 조회가 있다.
    * */
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}