package softuni.service;

import org.springframework.stereotype.Service;
import softuni.model.service.RoleServiceModel;

import java.util.Set;

@Service
public interface RoleService {

    void seedRolesInDb();

    Set<RoleServiceModel> findAllRoles();

    RoleServiceModel findByAuthority(String authority);
}
