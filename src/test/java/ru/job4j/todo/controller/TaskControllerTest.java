package ru.job4j.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.MIN;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskControllerTest {
    private TaskController taskController;
    private TaskService taskService;
    private List<Task> tasks;

    /**
     * Инициализировать taskService, taskController, списка задач tasks перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        taskController = new TaskController(taskService);
        var task1 = new Task();
        task1.setId(1);
        task1.setDescription("task1");
        task1.setCreated(LocalDateTime.of(
                LocalDate.of(MIN.getYear(), 1, 1),
                LocalTime.of(0, 0, 0)));
        task1.setDone(false);
        var task2 = new Task();
        task2.setId(2);
        task2.setDescription("task2");
        task2.setCreated(now());
        task2.setDone(true);
        var task3 = new Task();
        task2.setId(3);
        task2.setDescription("task3");
        task2.setCreated(now());
        task2.setDone(false);
        tasks = List.of(task1, task2, task3);
    }

    /**
     * Mock test getAll().
     * Вернуть страницу со всеми задачами сортированными по возрастанию по ID.
     */
    @Test
    void whenRequestTaskListPageThenGetPageWithTasksOrderedByIdAsc() {
        var expectedTasks = tasks;
        when(taskService.findAll()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAll(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    /**
     * Mock-test getCompleted().
     * Вернуть страницу со всеми выполненными задачами сортированными по возрастанию по ID.
     */
    @Test
    void whenRequestCompletedTaskListThenGetPageWithTasksOrderedByIdAsc() {
        var expectedTasks = List.of(tasks.get(1));
        when(taskService.findAllCompleted()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getCompleted(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/completed");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    /**
     * Mock-test getNew().
     * Вернуть страницу со всеми новыми задачами сортированными по возрастанию по ID.
     */
    @Test
    void whenRequestNewTaskListThenGetPageWithNewTasksOrderedByIdAsc() {
        var expectedTasks = List.of(tasks.get(1), tasks.get(2));
        when(taskService.findAllNew()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getNew(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/new");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    /**
     * Mock-test getCreationPage().
     * Вернуть страницу создания новой задачи.
     */
    @Test
    void whenRequestTaskCreationLPageThenGetIt() {
        var expectedView = "tasks/create";

        var view = taskController.getCreationPage();

        assertThat(view).isEqualTo(expectedView);
    }

    /**
     * Mock-test create().
     * Вернуть созданную задачу и перейти на страницу задач.
     */
    @Test
    void whenSaveTaskThenSameDataAndRedirectToTasksPage() {
        var task = tasks.get(0);
        var taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.save(taskArgumentCaptor.capture())).thenReturn(task);

        var model = new ConcurrentModel();
        var view = taskController.create(task, model);
        var actualTask = taskArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualTask).isEqualTo(task);
    }

    /**
     * Mock-test create().
     * Вернуть страницу об ошибке с сообщением при исключении во время создания новой заявки.
     */
    @Test
    void whenSaveTaskThenThrownExceptionThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to add new task");
        when(taskService.save(any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = taskController.create(any(), model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    /**
     * Mock-test getById().
     * Вернуть страницу искомой задачи.
     */
    @Test
    void whenRequestTaskByIdThenGetTaskPage() {
        var expectedTask = Optional.of(tasks.get(0));
        when(taskService.findById(1)).thenReturn(expectedTask);

        var model = new ConcurrentModel();
        var view = taskController.getById(1, model);
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(expectedTask.get());
    }

    /**
     * Mock-test getById().
     * Вернуть страницу об ошибке, если задача по ID не найдена.
     */
    @Test
    void whenRequestTaskByIllegalIdThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("""
                                                    Задача с указанными идентификатором не найдена.
                                                    """);
        var id = 0;
        when(taskService.findById(id)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = taskController.getById(id, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    /**
     * Mock-test delete().
     * Удалить задачу по ID и вернуть страницу tasks.
     */
    @Test
    void whenDeleteTaskByIdThenGetTrueAndTasksPage() {
        var isDeleted = true;
        var id = 1;
        when(taskService.delete(id)).thenReturn(isDeleted);

        var model = new ConcurrentModel();
        var view = taskController.delete(id, model);

        assertThat(view).isEqualTo("redirect:/tasks");
    }

    /**
     * Mock-test delete().
     * Вернуть страницу с описанием ошибки, при ошибке удаления задачи.
     */
    @Test
    void whenDeleteByIllegalIdThenGetFalseAndErrorPageWithMessage() {
        var isDeleted = false;
        var expectedException = new RuntimeException("""
                                                    Задача с указанными идентификатором не найдена.
                                                    """);
        var id = 0;
        when(taskService.delete(id)).thenReturn(isDeleted);

        var model = new ConcurrentModel();
        var view = taskController.delete(id, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    /**
     * Mock-test getEditPage().
     * Вернуть страницу редактирования задачи.
     */
    @Test
    void whenRequestEditPageThenGetPage() {
        var taskOptional = Optional.of(tasks.get(0));
        var id = 1;
        when(taskService.findById(id)).thenReturn(taskOptional);

        var model = new ConcurrentModel();
        var view = taskController.getEditPage(id, model);

        assertThat(view).isEqualTo("/tasks/edit");
    }

    /**
     * Mock-test getEditPage().
     * Вернуть страницу с указанием ошибки, в случае, если искомая задача не найдена.
     */
    @Test
    void whenRequestEditPageOfNotExistingTaskThenGetErrorPageWithMessage() {
        var expectedErrorMessage =
                                   """
                                   Задача с указанными идентификатором не найдена.
                                   """;
        Optional<Task> expectedOptional = Optional.empty();
        var id = 0;
        when(taskService.findById(0)).thenReturn(expectedOptional);

        var model = new ConcurrentModel();
        var view = taskController.getEditPage(id, model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test update().
     * Вернуть страницу tasks, при успешном обновлении задачи.
     */
    @Test
    void whenUpdateTaskThenGetTasksPage() {
        var updatedTask = tasks.get(0);
        var id = updatedTask.getId();
        updatedTask.setDescription("desc1 UPDATED");
        var taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.update(taskArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.update(id, updatedTask, model);
        var actualTask = taskArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualTask).isEqualTo(updatedTask);
    }

    /**
     * Mock-test update().
     * Вернуть страницу об ошибке, в случае, если задача не обновлена.
     * */
    @Test
    void whenUpdateNotExistingTaskThenGetErrorPageWithMessage() {
        var expectedErrorMessage = """
                Задача с указанными идентификатором не найдена.
                """;
        var id = 0;
        var taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.update(taskArgumentCaptor.capture())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = taskController.update(id, new Task(), model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test update().
     * Обработать выброшенный exception и выдать страницу ошибки с сообщением.
     */
    @Test
    void whenUpdateTaskThenThrownExceptionAndGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Not found");
        var id = 0;
        when(taskService.update(any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = taskController.update(id, new Task(), model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    /**
     * Mock-test updateStatus().
     * Вернуть страницу tasks, при успешном обновлении статуса задачи с "В процессе" на "Выполнено.
     */
    @Test
    void whenUpdateTaskStatusThenGetTasksPage() {
        var updatedTask = tasks.get(0);
        var id = updatedTask.getId();
        updatedTask.setDone(true);
        when(taskService.updateStatus(id)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.updateStatus(id, model);

        assertThat(view).isEqualTo("redirect:/tasks");
    }

    /**
     * Mock-test update().
     * Вернуть страницу об ошибке, в случае, если статус задачи не обновлен.
     */
    @Test
    void whenUpdateNotExistingTaskStatusThenGetErrorPageWithMessage1() {
        var expectedErrorMessage = """
                Задача с указанными идентификатором не найдена.
                """;
        var id = 0;
        when(taskService.updateStatus(id)).thenReturn(false);

        var model = new ConcurrentModel();
        var view = taskController.update(id, new Task(), model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    /**
     * Mock-test update().
     * Обработать выброшенный exception и выдать страницу ошибки с сообщением.
     */
    @Test
    void whenUpdateTaskThenThrownExceptionAndGetErrorPageWithMessage1() {
        var expectedException = new RuntimeException("Not found");
        var id = 0;
        when(taskService.updateStatus(id)).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = taskController.updateStatus(id, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

}