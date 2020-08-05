package softuni.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import softuni.service.RoleService;

@Component
public class DataInit implements CommandLineRunner {

    private final RoleService roleService;

    public DataInit(RoleService roleService) {

        this.roleService = roleService;
    }


    @Override
    public void run(String... args) throws Exception {
        roleService.seedRolesInDb();
    }
}
