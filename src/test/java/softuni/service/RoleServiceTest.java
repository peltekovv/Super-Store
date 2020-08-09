package softuni.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import softuni.model.entity.Role;
import softuni.model.service.RoleServiceModel;
import softuni.repository.RoleRepository;
import softuni.service.impl.RoleServiceImpl;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_BLQBLQ = "ROLE_BLQBLQ";

    private static List<Role> roleList;

    @InjectMocks
    RoleServiceImpl roleService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ModelMapper modelMapper;


    @Before
    public void init() {

        roleList = new ArrayList<>();

        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocationOnMock -> {
                    roleList.add((Role) invocationOnMock.getArguments()[0]);
                    return null;
                });

        ModelMapper mapper = new ModelMapper();
        when(modelMapper.map(any(Role.class),eq (RoleServiceModel.class)))
                .thenAnswer(invocationOnMock -> mapper.map(invocationOnMock.getArguments()[0], RoleServiceModel.class));
    }

    @Test
    public void seedRolesInDb_shouldSeedAllRoles_whenRepositoryIsEmpty() {

        //Arrange
        when(roleRepository.count()).thenReturn(0L);

        //Act
        roleService.seedRolesInDb();

        //Assert
        int expected = 3;
        assertEquals(expected,roleService.findAllRoles().size());
    }

    @Test
    public void seedRolesInDb_shouldNotSeedRoles_whenRepositoryNotEmpty() {

        //Arrange
        when(roleRepository.count()).thenReturn(4L);

        //Act
        roleService.seedRolesInDb();

        //Assert
        int expected = 0;
        assertEquals(expected, roleList.size());
    }

    @Test
    public void findAllRoles_shouldReturnAllCorrect_whenAnyRolesInDb() {

        //Arrange
        roleList = Arrays.asList(new Role(ROLE_ADMIN), new Role(ROLE_MODERATOR), new Role(ROLE_USER));
        when(roleRepository.findAll()).thenReturn(roleList);

        //Act
        Set<RoleServiceModel> found = roleService.findAllRoles();

        //Assert
        int expected = roleList.size();
        assertEquals(expected, found.size());
    }
    @Test
    public void findAllRoles_shouldReturnEmptySet_whenNoRolesInDb(){

        //Arrange
        when(roleRepository.findAll()).thenReturn(roleList);

        //Act
        Set<RoleServiceModel> found = roleService.findAllRoles();

        //Assert
        int expected = 0;
        assertEquals(expected, found.size());
    }

    @Test
    public void findByAuthority_shouldReturnCorrect_whenAuthorityExist(){

        //Arrange
        when(roleRepository.findByAuthority(ROLE_ADMIN))
                .thenReturn(Optional.of(new Role(ROLE_ADMIN)));

        //Act
        RoleServiceModel roleServiceModel = roleService.findByAuthority(ROLE_ADMIN);

        //Assert
        String actual = roleServiceModel.getAuthority();
        assertEquals(ROLE_ADMIN, actual);
    }

    @Test()
    public void findByAuthority_shouldThrowException_whenAuthorityNotExist() {

        //Arrange
        when(roleRepository.findByAuthority(ROLE_BLQBLQ))
                .thenReturn(Optional.empty());

        //Act
        RoleServiceModel byAuthority = roleService.findByAuthority(ROLE_BLQBLQ);


        //Assert
        assertEquals(byAuthority, null);
    }
}
