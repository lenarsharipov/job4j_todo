package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.model.Task;

/**
 * Контроллер TaskController
 *
 * @author Lenar Sharipov
 * @version 1.0
 */
@ThreadSafe
@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Вывести страницу со всеми задачами.
     * @param model модель
     * @return tasks/list.
     */
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    /**
     * Вывести страницу с завершенными задачами.
     * @param model модель.
     * @return tasks/completed
     */
    @GetMapping("/completed")
    public String getCompleted(Model model) {
        model.addAttribute("tasks", taskService.findAllCompleted());
        return "tasks/completed";
    }

    /**
     * Вывести страницу с новыми задачами.
     * @param model модель.
     * @return tasks/new.
     */
    @GetMapping("/new")
    public String getNew(Model model) {
        model.addAttribute("tasks", taskService.findAllNew());
        return "tasks/new";
    }

    /**
     * Перейти на страницу создания задачи.
     * @return tasks/create.
     */
    @GetMapping({"/", "/create"})
    public String getCreationPage() {
        return "tasks/create";
    }

    /**
     * Создать вакансию и перейти на страницу /tasks.
     * @param model модель.
     * @param task создаваемая задача.
     * @return "redirect:/tasks"
     */
    @PostMapping("/create")
    public String create(@ModelAttribute Task task, Model model) {
        try {
            taskService.save(task);
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    /**
     * Перейти на страницу задачи.
     * @param id ID.
     * @param model модель.
     * @return "tasks/one" или "errors/404".
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанными идентификатором не найдена.");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        return "tasks/one";
    }

    /**
     * Удалить задачу и перейти на страницу задач.
     * @param id ID.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        try {
            taskService.delete(id);
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    /**
     * Перейти на страницу задачи, которая будет редактироваться.
     * @param id ID.
     * @param model модель.
     * @return errors/404 или tasks/edit
     */
    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable int id, Model model) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанными идентификатором не найдена.");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        return "/tasks/edit";
    }

    /**
     * Отредактировать описание задачи.
     * @param id ID.
     * @param task задача.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @PostMapping("/edit/{id}")
    public String update(@PathVariable int id, @ModelAttribute Task task, Model model) {
        task.setId(id);
        try {
            var isUpdated = taskService.update(task);
            if (!isUpdated) {
                model.addAttribute("message", "Задача с указанным идентификатором не найдена");
                return "errors/404";
            }
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    /**
     * Обновить статус с "В процессе" на "Выполнено".
     * @param id ID.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @GetMapping("/complete/{id}")
    public String updateStatus(@PathVariable int id, Model model) {
        try {
            var isUpdated = taskService.updateStatus(id);
            if (!isUpdated) {
                model.addAttribute("message", "Задача с указанным идентификатором не найдена");
                return "errors/404";
            }
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

}