package toy.user.service;

import toy.user.model.Member;

public interface AuthService {
    void signUpUser(Member member);
    Member loginUser(String id, String password);
}