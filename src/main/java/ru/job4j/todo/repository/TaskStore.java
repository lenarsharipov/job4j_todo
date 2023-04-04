package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
     * Объект конфигуратор SessionFactory.
     */
    private final SessionFactory sf;
    private static final int DAYS_RANGE = 1;

    /**
     * Добавить новую заявку в БД.
     * @param task задача.
     * @return задача.
     */
    public Task save(Task task) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return task;
    }

    /**
     * Обновить задачу.
     * @param task задача.
     * @return true/false.
     */
    public boolean update(Task task) {
        var result = false;
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                    "UPDATE Task SET description = :fDescription WHERE id = :fId")
                    .setParameter("fDescription", task.getDescription())
                    .setParameter("fId", task.getId())
                    .executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
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
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                    "UPDATE Task SET done = true WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Вывести список всех задач из БД.
     * @return список задач.
     */
    public List<Task> findAll() {
        List<Task> result = Collections.emptyList();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("FROM Task ORDER BY id ASC", Task.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Вывести список всех завершенных задач.
     * @return список завершенных задач.
     */
    public List<Task> findAllCompleted(boolean done) {
        List<Task> result = Collections.emptyList();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                    "FROM Task WHERE done = :fDone ORDER BY id ASC", Task.class)
                    .setParameter("fDone", done)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Вывести список всех задач, созданных сегодня.
     * @return список задач.
     */
    public List<Task> findAllNew() {
        List<Task> result = Collections.emptyList();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                    "FROM Task WHERE created >= :fStart ORDER BY id ASC", Task.class)
                    .setParameter("fStart", LocalDateTime.now().minusDays(DAYS_RANGE))
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Вывести задачу, найденную по ID.
     * @param id ID.
     * @return Optional<Task>.
     */
    public Optional<Task> getById(int id) {
        Optional<Task> optionalTask = Optional.empty();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            optionalTask = session.createQuery(
                    "FROM Task WHERE id = :fId", Task.class)
                    .setParameter("fId", id)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        return optionalTask;
    }

    /**
     * Удалить задачу по ID.
     * @param id ID.
     * @return true/false.
     */
    public boolean delete(int id) {
        var result = false;
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("DELETE Task WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }
}