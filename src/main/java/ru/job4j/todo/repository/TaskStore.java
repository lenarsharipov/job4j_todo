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
import ru.job4j.todo.util.Entry;
import ru.job4j.todo.util.Key;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.TaskQuery;

/**
 * Task repository.
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
     * Create and save new task.
     * @param task task.
     * @return saved task.
     */
    public Optional<Task> save(Task task) {
        Optional<Task> result = Optional.empty();
        try {
            crudRepository.run(session -> session.save(task));
            result = Optional.of(task);
        } catch (Exception exception) {
            LOG.error(Message.TASK_NOT_SAVED, exception);
        }
        return result;
    }

    /**
     * Update task.
     * @param task task.
     * @return true/false.
     */
    public boolean update(Task task) {
        var result = true;
        try {
            crudRepository.run(session -> session.merge(task));
        } catch (Exception exception) {
            result = false;
            LOG.error(Message.TASK_NOT_UPDATED, exception);
        }
        return result;
    }

    /**
     * Update task status from false to true.
     * @param id ID.
     * @return true или false.
     */
    public boolean updateStatus(int id) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    TaskQuery.UPDATE_STATUS,
                    Map.of(Key.F_ID, id));
        } catch (Exception exception) {
            LOG.error(Message.STATUS_NOT_UPDATED, exception);
        }
        return result;
    }

    /**
     * List all persisted tasks.
     * @return list of tasks.
     */
    public List<Task> findAll() {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    String.format(Entry.TWO_ENTRIES,
                            TaskQuery.SELECT_DISTINCT,
                            TaskQuery.ORDER_BY_ID_ASC),
                    Task.class);
        } catch (Exception exception) {
            LOG.error(Message.TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * List all completed tasks.
     * @return list of tasks.
     */
    public List<Task> findAllCompleted(boolean done) {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    String.format(Entry.THREE_ENTRIES,
                            TaskQuery.SELECT_DISTINCT,
                            TaskQuery.WHERE_DONE,
                            TaskQuery.ORDER_BY_ID_ASC),
                    Task.class, Map.of(Key.F_DONE, done)
            );
        } catch (Exception exception) {
            LOG.error(Message.TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * List all new tasks. New task is a task added today.
     * @return list of tasks.
     */
    public List<Task> findAllNew() {
        List<Task> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    String.format(Entry.THREE_ENTRIES,
                            TaskQuery.SELECT_DISTINCT,
                            TaskQuery.WHERE_CREATED,
                            TaskQuery.ORDER_BY_ID_ASC),
                    Task.class, Map.of(Key.F_CREATED,
                            LocalDateTime.now().minusDays(DAYS_RANGE))
            );
        } catch (Exception exception) {
            LOG.error(Message.TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Get task specified by ID.
     * @param id ID.
     * @return Optional<Task>.
     */
    public Optional<Task> getById(int id) {
        Optional<Task> result = Optional.empty();
        try {
            result = crudRepository.optional(
                    String.format(Entry.TWO_ENTRIES,
                            TaskQuery.SELECT_DISTINCT,
                            TaskQuery.WHERE_ID),
                    Task.class, Map.of(Key.F_ID, id)
            );
        } catch (Exception exception) {
            LOG.error(Message.TASKS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Delete task by specified ID.
     * @param id ID.
     * @return true/false.
     */
    public boolean delete(int id) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    String.format(Entry.TWO_ENTRIES,
                            TaskQuery.DELETE_TASK,
                            TaskQuery.WHERE_ID),
                    Map.of(Key.F_ID, id)
            );
        } catch (Exception exception) {
            LOG.error(Message.TASK_NOT_DELETED, exception);
        }
        return result;
    }
}