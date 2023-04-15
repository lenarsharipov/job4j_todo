package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

/**
 * Хранилище задач.
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Repository
@AllArgsConstructor
public class TaskStore {
    /**
     ******************** MESSAGES ************************
     */
    private static final String TASK_NOT_SAVED = """
            Задача с указанным идентификатором не сохранена.
            """;
    private static final String TASK_NOT_UPDATED = """
            Задача с указанным идентификатором не обновлена.
            """;
    private static final String STATUS_NOT_UPDATED = """
            Статус задачи не обновлен.
            """;
    private static final String TASKS_NOT_FOUND = """
            Задачи не найдены.
            """;
    private static final String TASK_NOT_DELETED = """
            Задача с указанным идентификатором не удалена.
            """;

    private static final Logger LOG = LoggerFactory.getLogger(TaskStore.class.getName());

    private final CrudRepository crudRepository;

    private static final int DAYS_RANGE = 1;

    /**
     * Добавить новую заявку в БД.
     * @param task задача.
     * @return задача.
     */
    public Optional<Task> save(Task task) {
        Optional<Task> result = Optional.empty();
        try {
            crudRepository.run(session -> session.merge(task));
            result = Optional.of(task);
        } catch (Exception exception) {
            LOG.error(TASK_NOT_SAVED, exception);
        }
        return result;
    }

    /**
     * Обновить задачу.
     * @param task задача.
     * @return true/false.
     */
    public boolean update(Task task) {
        var result = true;
        try {
            crudRepository.run(session -> session.merge(task));
        } catch (Exception exception) {
            result = false;
            LOG.error(TASK_NOT_UPDATED, exception);
        }
        return result;
    }

    /**
     * Изменить статус задачи с false на true.
     * @param id ID.
     * @return true или false.
     */
    public boolean updateStatus(int id) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    "UPDATE Task SET done = true WHERE id = :fId",
                    Map.of("fId", id)
            );
        } catch (Exception exception) {
            LOG.error(STATUS_NOT_UPDATED, exception);
        }
        return result;
    }

    /**
     * Вывести список всех задач из БД.
     * @return список задач.
     */
    public List<Task> findAll() {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    """
                    SELECT DISTINCT t
                    FROM Task t
                    LEFT JOIN FETCH t.priority
                    LEFT JOIN FETCH t.categories
                    ORDER BY t.id ASC
                    """, Task.class);
        } catch (Exception exception) {
            LOG.error(TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Вывести список всех завершенных задач.
     * @return список завершенных задач.
     */
    public List<Task> findAllCompleted(boolean done) {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    """
                          SELECT DISTINCT t
                          FROM Task t
                          LEFT JOIN FETCH t.priority
                          LEFT JOIN FETCH t.categories
                          WHERE t.done = :fDone
                          ORDER BY t.id ASC
                          """, Task.class, Map.of("fDone", done)
            );
        } catch (Exception exception) {
            LOG.error(TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Вывести список всех задач, созданных сегодня.
     * @return список задач.
     */
    public List<Task> findAllNew() {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    """
                    SELECT DISTINCT t
                    FROM Task t
                    LEFT JOIN FETCH t.priority
                    LEFT JOIN FETCH t.categories
                    WHERE t.created >= :fCreated
                    ORDER BY t.id ASC
                    """, Task.class,
                    Map.of("fCreated", LocalDateTime.now().minusDays(DAYS_RANGE))
            );
        } catch (Exception exception) {
            LOG.error(TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Вывести задачу, найденную по ID.
     * @param id ID.
     * @return Optional<Task>.
     */
    public Optional<Task> getById(int id) {
        Optional<Task> result = Optional.empty();
        try {
            result = crudRepository.optional("""
                     SELECT DISTINCT t
                     FROM Task t
                     LEFT JOIN FETCH t.priority
                     LEFT JOIN FETCH t.categories
                     WHERE t.id = :fId
                     """, Task.class, Map.of("fId", id)
            );
        } catch (Exception exception) {
            LOG.error(TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Удалить задачу по ID.
     * @param id ID.
     * @return true/false.
     */
    public boolean delete(int id) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    "DELETE Task WHERE id = :fId",
                    Map.of("fId", id)
            );
        } catch (Exception exception) {
            LOG.error(TASK_NOT_DELETED, exception);
        }
        return result;
    }
}