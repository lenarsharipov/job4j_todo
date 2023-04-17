package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;
import ru.job4j.todo.util.Attribute;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.Page;

import javax.servlet.http.HttpSession;

/**
 * UserController.
 * @author Lenar Sharipov
 * @version 1.0
 */
@ThreadSafe
@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get User creation page.
     * @return users/register.
     */
    @GetMapping("/register")
    public String getRegistrationPage() {
        return Page.USERS_REGISTER;
    }

    /**
     * On success register new user and get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param model model.
     * @param user user.
     * @return redirect:/tasks or errors/404.
     */
    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        var optionalUser = userService.save(user);
        if (optionalUser.isEmpty()) {
            model.addAttribute(Attribute.MESSAGE, Message.NOT_UNIQUE_LOGIN);
            return Page.ERRORS_404;
        }
        model.addAttribute(Attribute.USER, user);
        return Page.REDIRECT_TASKS;
    }

    /**
     * Get authentication page.
     * @return users/login.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return Page.USERS_LOGIN;
    }

    /**
     * Sign in by login and password.
     * On success get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param user user.
     * @param model model.
     * @return redirect:/tasks or errors/404.
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpSession session) {
        var userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute(Attribute.MESSAGE, Message.LOGIN_PASSWORD_INCORRECT);
            return Page.ERRORS_404;
        }
        session.setAttribute(Attribute.USER, userOptional.get());
        return Page.REDIRECT_TASKS;
    }

    /**
     * Log out. And get redirected to redirect:/users/login page.
     * @param session HttpSession.
     * @return redirect:/users/login.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return Page.REDIRECT_USERS_LOGIN;
    }

}