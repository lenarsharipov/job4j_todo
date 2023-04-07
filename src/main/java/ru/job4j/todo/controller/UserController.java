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

import javax.servlet.http.HttpSession;

/**
 * Контроллер UserController.
 *
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
     * Вывести страницу регистрации пользователя.
     * @return users/register.
     */
    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    /**
     * Зарегистрировать нового пользователя и перейти на /tasks.
     * @param model модель.
     * @param user пользователь.
     * @return redirect:/tasks.
     */
    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        var optionalUser = userService.save(user);
        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует");
            return "errors/404";
        }
        model.addAttribute("user", user);
        return "redirect:/tasks";
    }

    /**
     * Вывести страницу аутентификации.
     * @return users/login.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    /**
     * Аутентифицировать пользователя и перейти на страницу tasks.
     * @param user пользователь.
     * @param model модель.
     * @return redirect:/tasks.
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpSession session) {
        var userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("message", "Почта или пароль введены неверно");
            return "errors/404";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/tasks";
    }

    /**
     * Выйти из системы.
     * @param session http-сессия.
     * @return redirect:/users/login.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

}