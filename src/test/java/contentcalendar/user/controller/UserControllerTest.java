package contentcalendar.user.controller;

import contentcalendar.user.domain.User;
import contentcalendar.user.repo.RoleRepo;
import contentcalendar.user.repo.UserRepo;
import contentcalendar.user.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@Slf4j
public class UserControllerTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private RoleRepo roleRepo;
    private UserServiceImpl underTest;

    @After
    public void tearDown() {
        userRepo.deleteAll();
    }

    @Before
    public void setUp() {
        underTest = new UserServiceImpl(this.userRepo, this.roleRepo);
        List<User> users = new ArrayList<>();

        users.add(new User(
                UUID.randomUUID(),
                "user1",
                "user1",
                "dudocjdc",
                null));
        users.add(new User(
                UUID.randomUUID(),
                "user2",
                "user2",
                "!!!!!!!!!!!",
                null
        ));

//        users.forEach(underTest::saveUser);
    }


    @Test
    public void itShouldReturnMoreThanOneUser() {
        underTest.getUsers();
        Mockito.verify(userRepo).findAll();
//        assertThat(users.size()).isNotEqualTo(1);
    }

    @Test
    public void itShouldNotFindUsername() {
        String username = "fakeUsername!!!";

        User findUser = underTest.getUser(username);

        Mockito.verify(userRepo).findByUsername(username);
    }

    @Test
    public void canSaveUser() {
        ArgumentCaptor<User> argumentCaptor;
        User user = new User(
                UUID.randomUUID(),
                "newUser",
                "newUser",
                "123",
                null);
        //when
        underTest.saveUser(user);
        //then
        argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    public void shouldFindUsername() {
        String username = "user1";
        ArgumentCaptor<User> userArgumentCaptor;

        User capturedUser;
        // when
        underTest.getUser(username);
        //then
        userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);
        userArgumentCaptor.capture();
        Mockito.verify(userRepo).findByUsername(username);
        capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isNotNull();
    }

    //    @Test
//    @Disabled
    public void itShouldSaveUser() {
        User findUser = underTest.getUser("user1");

        Mockito.verify(userRepo).findByUsername("user1");
    }


}