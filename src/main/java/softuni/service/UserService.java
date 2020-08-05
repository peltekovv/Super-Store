package softuni.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import softuni.model.entity.User;
import softuni.model.service.UserServiceModel;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserServiceModel register (UserServiceModel userServiceModel);

    UserServiceModel findByUsername(String username);

    UserServiceModel findByEmail(String email);

    List<UserServiceModel> findAllUsers();

    void setUserRole(Long id, String role);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel);
}
