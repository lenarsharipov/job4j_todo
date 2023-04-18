package ru.job4j.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;
import ru.job4j.todo.util.Attribute;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.Page;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserController userController;
    private UserService userService;
    private List<User> users;

    /**
     * Init taskService, taskController, List of tasks before each test.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        users = List.of(
                new User(1, "name1", "login1", "123456", "UTC"),
                new User(2, "name2", "login2", "123456", "UTC"),
                new User(3, "name3", "login3", "123456", "UTC"),
                new User(4, "name4", "login4", "123456", "UTC"));
    }

    /**
     * Mock-test getRegistrationPage().
     * Get user registration page.
     */
    @Test
    void whenRequestRegistrationPageThenGetIt() {
        var expectedView = Page.USERS_REGISTER;

        var model = new ConcurrentModel();
        var view = userController.getRegistrationPage(model);

        assertThat(view).isEqualTo(expectedView);
    }

    /**
     * Mock-test register().
     * Create new user and get /tasks page.
     */
    @Test
    void whenAddNewUserThenSameDataAndRedirectToTasksPage() {
        var user = users.get(0);
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo(Page.REDIRECT_TASKS);
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    /**
     * Mock-test register().
     * On failure to register new user with not-unique login get errors/404 page with error message.
     */
    @Test
    void whenAddNewUserWithNotUniqueLoginThenErrorPageWithMessage() {
        var expectedErrorMessage = Message.NOT_UNIQUE_LOGIN;
        var user = new User(0, "name111", "login1", "123456", "UTC");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualErrorMessage = model.getAttribute(Attribute.MESSAGE);

        assertThat(view).isEqualTo(Page.ERRORS_404);
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test getLoginPage().
     * Get sign in page.
     */
    @Test
    public void whenRequestLoginPageThenGetIt() {
        var expectedView = Page.USERS_LOGIN;

        var view = userController.getLoginPage();

        assertThat(view).isEqualTo(expectedView);
    }

    /**
     * Mock-test loginUser().
     * Sign in and get /tasks page.
     */
    @Test
    public void whenRequestToLoginWithCorrectDataThenLoggedInAndRedirectedToTasksPage() {
        var user = users.get(0);
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, session);

        assertThat(view).isEqualTo(Page.REDIRECT_TASKS);
    }

    /**
     * Mock-test loginUser().
     * On failure to sign in with incorrect login or password
     * get errors/404 page with error message.
     */
    @Test
    public void whenRequestToLoginWithInCorrectDataThenErrorPageWithMessage() {
        var expectedErrorMessage = Message.LOGIN_PASSWORD_INCORRECT;
        var user = new User(0, "name", "login", "password", "UTC");
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, session);
        var actualErrorMessage = model.getAttribute(Attribute.MESSAGE);

        assertThat(view).isEqualTo(Page.ERRORS_404);
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test logout().
     * Sign out and get redirected to /users/login page.
     */
    @Test
    void whenRequestToLogoutThenLoggedOutAndRedirectedToLoginPage() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);

        assertThat(view).isEqualTo(Page.REDIRECT_USERS_LOGIN);
    }
}