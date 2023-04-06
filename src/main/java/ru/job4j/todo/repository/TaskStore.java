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
            crudRepository.run(session -> session.persist(task));
            result = Optional.of(task);
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
        }
        return result;
    }

    /**
     * Обновить задачу.
     * @param task задача.
     * @return true/false.
     */
    public boolean update(Task task) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    "UPDATE Task SET description = :fDescription WHERE id = :fId",
                    Map.of("fDescription", task.getDescription(),
                            "fId", task.getId())
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
            LOG.error("Exception in log example", exception);
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
                    "FROM Task ORDER BY id ASC", Task.class
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
                    "FROM Task WHERE done = :fDone ORDER BY id ASC", Task.class,
                    Map.of("fDone", done)
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
                    "FROM Task WHERE created >= :fDone ORDER BY id ASC", Task.class,
                    Map.of("fDone", LocalDateTime.now().minusDays(DAYS_RANGE))
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
            result = crudRepository.optional(
                    "FROM Task WHERE id = :fId", Task.class,
                    Map.of("fId", id)
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
            LOG.error("Exception in log example", exception);
        }
        return result;
    }
}