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
import ru.job4j.todo.util.Attribute;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskController
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private static final boolean FLAG = true;
    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    /**
     * List all existing tasks.
     * @param model model.
     * @return tasks/list.
     */
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute(Attribute.TASKS, taskService.findAll());
        return Page.TASKS_LIST;
    }

    /**
     * List all completed tasks existing in DB.
     * @param model model.
     * @return tasks/completed.
     */
    @GetMapping("/completed")
    public String getCompleted(Model model) {
        model.addAttribute(Attribute.TASKS, taskService.findAllCompleted(FLAG));
        return Page.TASKS_COMPLETED;
    }

    /**
     * List all new tasks existing in DB.
     * @param model model.
     * @return tasks/new.
     */
    @GetMapping("/new")
    public String getNew(Model model) {
        model.addAttribute(Attribute.TASKS, taskService.findAllNew());
        return Page.TASKS_NEW;
    }

    /**
     * Get Task creation page.
     * @return tasks/create.
     */
    @GetMapping({"/", "/create"})
    public String getCreationPage(Model model) {
        model.addAttribute(Attribute.TASK, new Task());
        model.addAttribute(Attribute.PRIORITIES, priorityService.findAll());
        model.addAttribute(Attribute.CATEGORIES, categoryService.findAll());
        return Page.TASKS_CREATE;
    }

    /**
     * Convert array of Strings into array of Integers.
     * Map new Categories with ids from Integer array.
     * @param task task.
     * @param request HttpServletRequest.
     */
    private void addCategories(Task task, HttpServletRequest request) {
        var categoryIds = request.getParameterValues(Attribute.CATEGORY_IDS);
        for (var categoryId : categoryIds) {
            var id = Integer.parseInt(categoryId);
            task.getCategories().add(new Category(id));
        }
    }

    /**
     * Create new task and save it in DB. Then get redirected to /tasks page.
     * On success get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param model model.
     * @param task new task.
     * @return "redirect:/tasks".
     */
    @PostMapping("/create")
    public String create(Task task, Model model, HttpServletRequest request) {
        var user = (User) request.getAttribute(Attribute.USER);
        task.setUser(user);
        addCategories(task, request);
        var taskOptional = taskService.save(task);
        if (taskOptional.isEmpty()) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_SAVED);
            return Page.ERRORS_404;
        }
        return Page.REDIRECT_TASKS;
    }

    /**
     * Get task page. Task specified by ID.
     * On success get /tasks/one page.
     * On failure get errors/404 page with error message.
     * @param id ID.
     * @param model model.
     * @return "tasks/one" or "errors/404".
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_FOUND);
            return Page.ERRORS_404;
        }
        model.addAttribute(Attribute.TASK, taskOptional.get());
        return Page.TASKS_ONE;
    }

    /**
     * Delete Task by specified ID. Then get redirected to /tasks page.
     * On success get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param id ID.
     * @param model model.
     * @return redirect:/tasks or errors/404.
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        var isDeleted = taskService.delete(id);
        if (!isDeleted) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_FOUND);
            return Page.ERRORS_404;
        }
        return Page.REDIRECT_TASKS;
    }

    /**
     * Util method that retrieves IDs from passed task categories.
     * @param categories task categories.
     * @return list of selected category Ids.
     */
    private List<Integer> getCategoryIds(List<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    /**
     * Get task's edit page. Task specified by ID.
     * On success get /tasks/edit page.
     * On failure get errors/404 page with error message.
     * @param id ID.
     * @param model model.
     * @return errors/404 or tasks/edit.
     */
    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable int id, Model model) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_FOUND);
            return Page.ERRORS_404;
        }
        var priorities = priorityService.findAll();
        var categories = categoryService.findAll();
        var task = taskOptional.get();
        var selectedIds = getCategoryIds(task.getCategories());
        model.addAttribute(Attribute.SELECTED_IDS, selectedIds);
        model.addAttribute(Attribute.TASK, task);
        model.addAttribute(Attribute.PRIORITIES, priorities);
        model.addAttribute(Attribute.CATEGORIES, categories);
        return Page.TASKS_EDIT;
    }

    /**
     * Edit and Update specified Task.
     * On success get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param task task.
     * @param model model.
     * @return redirect:/tasks or errors/404.
     */
    @PostMapping("/edit/{id}")
    public String update(@ModelAttribute Task task, Model model, HttpServletRequest request) {
        addCategories(task, request);
        var isUpdated = taskService.update(task);
        if (!isUpdated) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_UPDATED);
            return Page.ERRORS_404;
        }
        return Page.REDIRECT_TASKS;
    }

    /**
     * Update task status. Change it only from "In progress" to "Completed".
     * On success get redirected to /tasks page.
     * On failure get errors/404 page with error message.
     * @param id ID.
     * @param model model.
     * @return redirect:/tasks or errors/404.
     */
    @GetMapping("/complete/{id}")
    public String updateStatus(@PathVariable int id, Model model) {
        var isUpdated = taskService.updateStatus(id);
        if (!isUpdated) {
            model.addAttribute(Attribute.MESSAGE, Message.TASK_NOT_UPDATED);
            return Page.ERRORS_404;
        }
        return Page.REDIRECT_TASKS;
    }
}