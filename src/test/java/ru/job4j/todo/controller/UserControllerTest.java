package ru.job4j.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

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
     * Инициализировать taskService, taskController, списка задач tasks перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        users = List.of(
                new User(1, "name1", "login1", "123456"),
                new User(2, "name2", "login2", "123456"),
                new User(3, "name3", "login3", "123456"),
                new User(4, "name4", "login4", "123456"));
    }

    /**
     * Mock-test getRegistrationPage().
     * Вывести страницу регистрации.
     */
    @Test
    void whenRequestRegistrationPageThenGetIt() {
        var expectedView = "users/register";

        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo(expectedView);
    }

    /**
     * Mock-test register().
     * Создать нового пользователя и перейти на страницу задач.
     */
    @Test
    void whenAddNewUserThenSameDataAndRedirectToTasksPage() {
        var user = users.get(0);
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    /**
     * Mock-test register().
     * Перейти на страницу об ошибке, при добавлении пользователя с не уникальным логином.
     */
    @Test
    void whenAddNewUserWithNotUniqueLoginThenErrorPageWithMessage() {
        var expectedErrorMessage = "Пользователь с такой почтой уже существует";
        var user = new User(0, "name111", "login1", "123456");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test getLoginPage().
     * Вывести страницу аутентификации.
     */
    @Test
    public void whenRequestLoginPageThenGetIt() {
        var expectedView = "users/login";

        var view = userController.getLoginPage();

        assertThat(view).isEqualTo(expectedView);
    }

    /**
     * Mock-test loginUser().
     * Аутентифицироваться и перейти на страницу tasks.
     */
    @Test
    public void whenRequestToLoginWithCorrectDataThenLoggedInAndRedirectedToTasksPage() {
        var user = users.get(0);
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, session);

        assertThat(view).isEqualTo("redirect:/tasks");
    }

    /**
     * Mock-test loginUser().
     * При аутентификации пользователя по неверным данным, перейти на страницу с ошибкой.
     */
    @Test
    public void whenRequestToLoginWithInCorrectDataThenErrorPageWithMessage() {
        var expectedErrorMessage = "Почта или пароль введены неверно";
        var user = new User(0, "name", "login", "password");
        var session = mock(HttpSession.class);
        when(userService.findByLoginAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, session);
        var actualErrorMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test logout().
     * Выйти из системы и перейти на страницу аутентификации.
     */
    @Test
    void whenRequestToLogoutThenLoggedOutAndRedirectedToLoginPage() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
    }
}