package softuni.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import softuni.model.entity.Role;
import softuni.model.entity.User;
import softuni.model.service.RoleServiceModel;
import softuni.model.service.UserServiceModel;
import softuni.repository.UserRepository;
import softuni.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserServiceTestt {

    private static final UserServiceModel MODEL = new UserServiceModel();
    private static final User USER = new User();

    private static final String VALID_USERNAME = "Niko";
    private static final String VALID_PASSWORD = "123456";
    private static final String VALID_EDITED_PASSWORD = "1234567";
    private static final String VALID_EMAIL = "dj_as@abv.bg";
    private static final Long VALID_ID = 2L;
    private static final String VALID_EDITED_EMAIL = "dj_abv@abv.bg";

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    BCryptPasswordEncoder encoder;


    @Before
    public void init() {

        ModelMapper actualMapper = new ModelMapper();
        BCryptPasswordEncoder actualEncoder = new BCryptPasswordEncoder();

        when(modelMapper.map(any(UserServiceModel.class), eq(User.class)))
                .thenAnswer(invocationOnMock ->
                        actualMapper.map(invocationOnMock.getArguments()[0], User.class));

        when(modelMapper.map(any(User.class), eq(UserServiceModel.class)))
                .thenAnswer(invocationOnMock ->
                        actualMapper.map(invocationOnMock.getArguments()[0], UserServiceModel.class));

        when(roleService.findByAuthority(anyString()))
                .thenAnswer(invocationOnMock ->
                        actualMapper.map(new Role((String) invocationOnMock.getArguments()[0]),
                                RoleServiceModel.class));


        when(encoder.encode(any())).thenAnswer(invocationOnMock ->
                actualEncoder.encode((CharSequence) invocationOnMock.getArguments()[0]));
        when(encoder.matches(any(), any())).thenAnswer(invocationOnMock ->
                actualEncoder.matches((String) invocationOnMock.getArguments()[0],
                        (String) invocationOnMock.getArguments()[1]));

        USER.setUsername(VALID_USERNAME);
        USER.setPassword(VALID_PASSWORD);
        USER.setEmail(VALID_EMAIL);

        MODEL.setUsername(VALID_USERNAME);
        MODEL.setPassword(VALID_PASSWORD);
        MODEL.setEmail(VALID_EMAIL);
        MODEL.setAuthorities(Set.of(new RoleServiceModel()));
    }

    @Test
    public void registerUser_WhenFirstValidUserIsAdded_ShouldWork()  {

        when(userRepository.count()).thenReturn(0L);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(USER);

        UserServiceModel serviceModel = userService.register(MODEL);

        assertNotNull(MODEL);
        assertEquals(USER.getUsername(), serviceModel.getUsername());
    }

    @Test
    public void registerUser_WhenNotFirstValidUserIsAdded_ShouldWork() {

        String authority = "ROLE_CUSTOMER";
        RoleServiceModel mockRole = new RoleServiceModel() {{
            new Role(authority);
        }};

        when(userRepository.count()).thenReturn(1L);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(USER);
        when(roleService.findByAuthority(authority)).thenReturn(mockRole);

        UserServiceModel serviceModel = userService.register(MODEL);

        assertNotNull(serviceModel);
        assertEquals(USER.getUsername(), serviceModel.getUsername());

    }

    @Test()
    public void findByUsername_WhenNotExistUser_ShouldThrow() {
        String username = "Peter";

        UserServiceModel byUsername = userService.findByUsername(username);

        assertNull(byUsername);
    }

    @Test
    public void findByUsername_WhenUserExist_ShouldWork() {

        when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(USER));

        UserServiceModel serviceModel = userService.findByUsername(VALID_USERNAME);

        assertEquals(VALID_USERNAME, serviceModel.getUsername());
    }

    @Test()
    public void findByEmail_WhenNotExistUser_ShouldThrow() {
        String email = "Peter";

        UserServiceModel byEmail = userService.findByEmail(email);

        assertNull(byEmail);
    }

    @Test
    public void findByEmail_WhenUserExist_ShouldWork() {

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(USER));

        UserServiceModel serviceModel = userService.findByEmail(VALID_EMAIL);

        assertEquals(VALID_EMAIL, serviceModel.getEmail());
    }


    @Test
    public void findAllUser_WhenNotHaveUsers_ShouldReturnEmptyList() {

        List<UserServiceModel> allUsers = userService.findAllUsers();

        assertEquals(0, allUsers.size());
    }



    @Test(expected = IllegalArgumentException.class)
    public void setUserRole_WhenUserWithIdNotExist_ShouldThrow()  {

        Long invalidId = 5L;
        String role = "moderator";

        userService.setUserRole(invalidId, role);
    }

    @Test
    public void setUserRole_WhenUserIsPromotedToModerator_ShouldHaveRoleModerator()  {

        Set<Role> auth = Set.of(new Role("ROLE_MODERATOR"));
        String role = "moderator";
        USER.setAuthorities(auth);

        when(userRepository.findById(2L)).thenReturn(Optional.of(USER));

        userService.setUserRole(2L, role);



        UserServiceModel currUser = this.modelMapper.map(userRepository.findById(2L).get(),UserServiceModel.class);

        List<RoleServiceModel> currUserAuthorities = new ArrayList<>(currUser.getAuthorities());

        assertEquals(1, currUser.getAuthorities().size());
        assertEquals("ROLE_MODERATOR", currUserAuthorities.get(0).getAuthority());
    }

    @Test
    public void setUserRole_WhenUserIsPromotedToAdmin_ShouldHaveRolesAndAdmin() {

        Set<Role> auth = Set.of(new Role("ROLE_ADMIN"));
        String role = "admin";
        USER.setAuthorities(auth);

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(USER));

        userService.setUserRole(VALID_ID, role);

        UserServiceModel currUser = this.modelMapper.map(userRepository.findById(2L).get(),UserServiceModel.class);


        assertEquals(1, currUser.getAuthorities().size());
    }
}
