package toy.user.service;

import toy.user.entity.User;

public interface AuthService {
    void signUpUser(User member);
    User loginUser(String id, String password);
}