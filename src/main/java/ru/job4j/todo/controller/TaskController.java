package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpServletRequest;

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

    /**
     ******************** MESSAGES ************************
     */
    private static final String TASK_NOT_FOUND = """
            Задача с указанными идентификатором не найдена.
            """;
    private static final String TASK_NOT_SAVED = """
            Задача с указанными идентификатором не сохранена.
            """;
    private static final String TASK_NOT_UPDATED = """
            Задача с указанными идентификатором не обновлена.
            """;

    /**
     ******************** PAGES ************************
     */
    private static final String ERRORS_404 = "errors/404";
    private static final String REDIRECT_TASKS = "redirect:/tasks";
    private static final String TASKS_LIST = "tasks/list";
    private static final String TASKS_COMPLETED = "tasks/completed";
    private static final String TASKS_NEW = "tasks/new";
    private static final String TASKS_CREATE = "tasks/create";
    private static final String TASKS_ONE = "tasks/one";
    private static final String TASKS_EDIT = "/tasks/edit";

    /**
     ******************** ATTRIBUTES ************************
     */
    private static final String MESSAGE = "message";
    private static final String TASKS = "tasks";
    private static final String TASK = "task";
    private static final String USER = "user";
    private static final String CATEGORIES = "categories";
    private static final String PRIORITIES = "priorities";
    private static final String CATEGORY_IDS = "categoryIds";

    private static final boolean FLAG = true;

    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    /**
     * Вывести страницу со всеми задачами.
     * @param model модель.
     * @return tasks/list.
     */
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute(TASKS, taskService.findAll());
        return TASKS_LIST;
    }

    /**
     * Вывести страницу с завершенными задачами.
     * @param model модель.
     * @return tasks/completed.
     */
    @GetMapping("/completed")
    public String getCompleted(Model model) {
        model.addAttribute(TASKS, taskService.findAllCompleted(FLAG));
        return TASKS_COMPLETED;
    }

    /**
     * Вывести страницу с новыми задачами.
     * @param model модель.
     * @return tasks/new.
     */
    @GetMapping("/new")
    public String getNew(Model model) {
        model.addAttribute(TASKS, taskService.findAllNew());
        return TASKS_NEW;
    }

    /**
     * Перейти на страницу создания задачи.
     * @return tasks/create.
     */
    @GetMapping({"/", "/create"})
    public String getCreationPage(Model model) {
        model.addAttribute(TASK, new Task());
        model.addAttribute(PRIORITIES, priorityService.findAll());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        return TASKS_CREATE;
    }

    /**
     * Перевести ID полученных категорий из массива строк в массив int и добавить в Task.
     * @param task задача
     * @param request HttpServletRequest
     */
    private void addCategories(Task task, HttpServletRequest request) {
        var categoryIds = request.getParameterValues(CATEGORY_IDS);
        for (var categoryId : categoryIds) {
            var id = Integer.parseInt(categoryId);
            task.getCategories().add(new Category(id));
        }
    }

    /**
     * Создать задачу и перейти на страницу /tasks.
     * @param model модель.
     * @param task создаваемая задача.
     * @return "redirect:/tasks".
     */
    @PostMapping("/create")
    public String create(Task task, Model model, HttpServletRequest request) {
        var user = (User) request.getAttribute(USER);
        task.setUser(user);
        addCategories(task, request);
        var taskOptional = taskService.save(task);
        if (taskOptional.isEmpty()) {
            model.addAttribute(MESSAGE, TASK_NOT_SAVED);
            return ERRORS_404;
        }
        return REDIRECT_TASKS;
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
            model.addAttribute(MESSAGE, TASK_NOT_FOUND);
            return ERRORS_404;
        }
        model.addAttribute(TASK, taskOptional.get());
        return TASKS_ONE;
    }

    /**
     * Удалить задачу и перейти на страницу задач.
     * @param id ID.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        var isDeleted = taskService.delete(id);
        if (!isDeleted) {
            model.addAttribute(MESSAGE, TASK_NOT_FOUND);
            return ERRORS_404;
        }
        return REDIRECT_TASKS;
    }

    /**
     * Перейти на страницу редактирования задачи, которая будет отредактирована.
     * @param id ID.
     * @param model модель.
     * @return errors/404 или tasks/edit.
     */
    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable int id, Model model) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute(MESSAGE, TASK_NOT_FOUND);
            return ERRORS_404;
        }
        var priorities = priorityService.findAll();
        var categories = categoryService.findAll();
        model.addAttribute(PRIORITIES, priorities);
        model.addAttribute(CATEGORIES, categories);
        model.addAttribute(TASK, taskOptional.get());
        return TASKS_EDIT;
    }

    /**
     * Отредактировать описание задачи.
     * @param task задача.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @PostMapping("/edit/{id}")
    public String update(@ModelAttribute Task task, Model model, HttpServletRequest request) {
        addCategories(task, request);
        var isUpdated = taskService.update(task);
        if (!isUpdated) {
            model.addAttribute(MESSAGE, TASK_NOT_UPDATED);
            return ERRORS_404;
        }
        return REDIRECT_TASKS;
    }

    /**
     * Обновить статус с "В процессе" на "Выполнено".
     * @param id ID.
     * @param model модель.
     * @return redirect:/tasks или errors/404.
     */
    @GetMapping("/complete/{id}")
    public String updateStatus(@PathVariable int id, Model model) {
        var isUpdated = taskService.updateStatus(id);
        if (!isUpdated) {
            model.addAttribute(MESSAGE, TASK_NOT_UPDATED);
            return ERRORS_404;
        }
        return REDIRECT_TASKS;
    }
}