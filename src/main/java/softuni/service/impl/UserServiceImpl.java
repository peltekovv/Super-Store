package softuni.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import softuni.model.entity.Role;
import softuni.model.entity.User;
import softuni.model.service.RoleServiceModel;
import softuni.model.service.UserServiceModel;
import softuni.repository.UserRepository;
import softuni.service.RoleService;
import softuni.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(RoleService roleService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper) {
        this.roleService = roleService;

        this.userRepository = userRepository;

        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));
    }

    @Override
    public UserServiceModel register(UserServiceModel userServiceModel) {

        User user = this.modelMapper.map(userServiceModel,User.class);

        if(userRepository.count() == 0 ){
            Role root_admin1 = this.modelMapper.map(roleService.findByAuthority("ROLE_MODERATOR"),Role.class);
            Role admin1 = this.modelMapper.map(roleService.findByAuthority("ROLE_ADMIN"),Role.class);
            Role user1 = this.modelMapper.map(roleService.findByAuthority("ROLE_USER"),Role.class);


            Set<Role> roles = new LinkedHashSet<>();
            roles.add(root_admin1);
            roles.add(admin1);
            roles.add(user1);

            user.setAuthorities(roles);

        }else{
            Role user1 = this.modelMapper.map(roleService.findByAuthority("ROLE_USER"),Role.class);

            Set<Role> roles = new LinkedHashSet<>();

            roles.add(user1);
            user.setAuthorities(roles);
        }



        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        return this.modelMapper.map(user,UserServiceModel.class);
    }

    @Override
    public UserServiceModel findByUsername(String username) {
        if(this.userRepository.findByUsername(username).isPresent()){
            Optional<User> byUsername = userRepository.findByUsername(username);
            UserServiceModel userServiceModel = new UserServiceModel();
            userServiceModel.setId(byUsername.get().getId());
            userServiceModel.setAuthorities(transfAuthority(byUsername.get().getAuthorities()));
            userServiceModel.setPassword(byUsername.get().getPassword());
            userServiceModel.setUsername(byUsername.get().getUsername());
            userServiceModel.setEmail(byUsername.get().getEmail());
            return userServiceModel;
        }else{
            return null;
        }

    }

    @Override
    public UserServiceModel findByEmail(String email) {
        if(this.userRepository.findByEmail(email).isPresent()){
            Optional<User> byUsername = userRepository.findByEmail(email);
            UserServiceModel userServiceModel = new UserServiceModel();
            userServiceModel.setId(byUsername.get().getId());
            userServiceModel.setAuthorities(transfAuthority(byUsername.get().getAuthorities()));
            userServiceModel.setPassword(byUsername.get().getPassword());
            userServiceModel.setUsername(byUsername.get().getUsername());
            userServiceModel.setEmail(byUsername.get().getEmail());
            return userServiceModel;
        }else{
            return null;
        }
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        return this.userRepository.findAll().stream()
                .map(u -> this.modelMapper.map(u, UserServiceModel.class)).collect(Collectors.toList());

    }

    @Override
    public void setUserRole(Long id, String role) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect id!"));

        UserServiceModel userServiceModel = this.modelMapper.map(user, UserServiceModel.class);
        userServiceModel.getAuthorities().clear();


        switch (role) {
            case "user":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                break;
            case "moderator":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_MODERATOR"));
                break;
            case"admin":
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_MODERATOR"));
                userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_ADMIN"));
        }


        this.userRepository.saveAndFlush(this.modelMapper.map(userServiceModel, User.class));
    }

    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("Username not found!"));


        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));


        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    private Set<RoleServiceModel> transfAuthority(Collection<Role> authorities) {
        Set<RoleServiceModel> roleServiceModels = new LinkedHashSet<>();

        for (Role authority : authorities) {
           roleServiceModels.add(this.modelMapper.map(authority,RoleServiceModel.class));
        }
        return roleServiceModels;
    }
}
