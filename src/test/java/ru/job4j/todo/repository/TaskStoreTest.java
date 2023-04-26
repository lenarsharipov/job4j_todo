package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStoreTest implements AutoCloseable {
    private static final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private static final SessionFactory SESSION_FACTORY = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();
    private static final CrudRepository CRUD_REPOSITORY = new CrudRepository(SESSION_FACTORY);
    private static final TaskStore TASK_STORE = new TaskStore(CRUD_REPOSITORY);
    private static final UserStore USER_STORE = new UserStore(CRUD_REPOSITORY);
    private static final PriorityStore PRIORITY_STORE = new PriorityStore(CRUD_REPOSITORY);
    private static final CategoryStore CATEGORY_STORE = new CategoryStore(CRUD_REPOSITORY);
    private static Priority normal;
    private static Category hobby;
    private static Category friends;
    private static Task task;

    @BeforeAll
    static void init() {
        User admin = USER_STORE.findAll().get(0);
        var priorities = PRIORITY_STORE.findAll();
        Priority veryHigh = priorities.get(0);
        normal = priorities.get(2);
        var categories = CATEGORY_STORE.findAll();
        Category home = categories.get(0);
        hobby = categories.get(1);
        Category sport = categories.get(5);
        friends = categories.get(6);

        task = new Task();
        task.setDescription("desc1");
        task.setUser(admin);
        task.setPriority(veryHigh);
        task.setCategories(List.of(home, sport));
    }

    @BeforeEach
    void clear() {
        var tasks = TASK_STORE.findAll();
        for (var task : tasks) {
            TASK_STORE.delete(task.getId());
        }
    }

    /**
     * Save Task and get not empty optional. Saved Task gets generated ID.
     */
    @Test
    void whenSaveThenSavedAndGetNotEmptyOptional() {
        var taskOptional = TASK_STORE.save(task);

        assertThat(taskOptional).isNotEmpty();
        assertThat(taskOptional.get()).isEqualTo(task);
        assertThat(TASK_STORE.findAll()).isEqualTo(List.of(task));
    }

    /**
     * Update Task and get true.
     */
    @Test
    void whenUpdateTaskThenUpdatedAndGetTrue() {
        var taskOptional = TASK_STORE.save(task);
        assertThat(taskOptional).isNotEmpty();
        var updated = taskOptional.get();
        updated.setDescription("UPDATED");
        updated.setPriority(normal);
        updated.setCategories(List.of(hobby, friends));

        assertThat(TASK_STORE.update(updated)).isTrue();
        assertThat(TASK_STORE.findAll()).isEqualTo(List.of(updated));
        assertThat(TASK_STORE.findAll().get(0)).isEqualTo(updated).usingRecursiveComparison();
    }

    /**
     * Update Task status from "In progress" to "Completed" and get true.
     */
    @Test
    void whenUpdateStatusThenGetTrue() {
        var taskOptional = TASK_STORE.save(task);
        assertThat(taskOptional).isNotEmpty();
        var id = taskOptional.get().getId();

        assertThat(TASK_STORE.updateStatus(id)).isTrue();
        var foundTask = TASK_STORE.getById(id);
        assertThat(foundTask).isNotEmpty();
        assertThat(foundTask.get().isDone()).isTrue();
    }

    /**
     * Get list of all saved Tasks.
     */
    @Test
    void whenFindAllThenGetListOfSavedTasks() {
        TASK_STORE.save(task);
        assertThat(TASK_STORE.findAll()).isEqualTo(List.of(task));
    }

    /**
     * Get list of all Tasks with "completed" status.
     */
    @Test
    void whenListCompletedThenGetIt() {
        task.setDone(true);
        TASK_STORE.save(task);
        assertThat(TASK_STORE.findAllCompleted(true)).isEqualTo(List.of(task));
    }

    /**
     * Get empty list if no Task completed.
     */
    @Test
    void whenListCompletedThenGetEmptyList() {
        task.setDone(false);
        TASK_STORE.save(task);
        assertThat(TASK_STORE.findAllCompleted(true)).isEmpty();
    }

    /**
     * Get list of newly added Tasks.
     */
    @Test
    void whenFindNewThenListNewTasks() {
        task.setCreated(LocalDateTime.now());
        TASK_STORE.save(task);
        assertThat(TASK_STORE.findAllNew()).isEqualTo(List.of(task));
    }

    /**
     * Get empty list if no new Tasks.
     */
    @Test
    void whenFindNewThenListEmptyList() {
        var created = LocalDateTime.now().withYear(2000);
        task.setCreated(created);
        TASK_STORE.save(task);
        assertThat(TASK_STORE.findAllNew()).isEmpty();
    }

    /**
     * Get Task by ID.
     */
    @Test
    void whenGetByIdThenGetIt() {
        TASK_STORE.save(task);
        var id = task.getId();
        var taskOptional = TASK_STORE.getById(id);
        assertThat(taskOptional).isNotEmpty();
        assertThat(taskOptional.get()).isEqualTo(task).usingRecursiveComparison();
    }

    /**
     * Get empty optional if Task not found by ID.
     */
    @Test
    void whenGetByIncorrectIdThenGetEmptyOptional() {
        var id = -1;
        assertThat(TASK_STORE.getById(id)).isEmpty();
    }

    /**
     * Get true if Task deleted by ID.
     */
    @Test
    void whenDeleteThenDeletedAndTrue() {
        TASK_STORE.save(task);
        var id = task.getId();
        assertThat(TASK_STORE.delete(id)).isTrue();
        assertThat(TASK_STORE.findAll()).isEmpty();
    }

    /**
     * Get false if Task not deleted by ID.
     */
    @Test
    void whenDeleteByIncorrectIdThenGetSameList() {
        TASK_STORE.save(task);
        var id = -1;
        assertThat(TASK_STORE.delete(id)).isFalse();
        assertThat(TASK_STORE.findAll()).isEqualTo(List.of(task));
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(REGISTRY);
    }
}