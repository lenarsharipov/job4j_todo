package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище пользователей.
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Repository
@AllArgsConstructor
public class UserStore {

    /**
     * Объект конфигуратор SessionFactory.
     */
    private final SessionFactory sf;

    /**
     * Добавить нового пользователя.
     * @param user пользователь.
     * @return пользователь.
     */
    public User save(User user) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return user;
    }

    /**
     * Вывести пользователя, найденного по логину и паролю.
     * @param login логин.
     * @param password пароль.
     * @return Optional<User>.
     */
    public Optional<User> findByLoginAndPassword(String login, String password) {
        Optional<User> result = Optional.empty();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery(
                    "FROM User WHERE login = :fLogin AND password = :fPassword",
                            User.class)
                    .setParameter("fLogin", login)
                    .setParameter("fPassword", password)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Вывести всех пользователей.
     * @return список пользователей.
     */
    public List<User> findAll() {
        List<User> result = Collections.emptyList();
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("FROM User ORDER BY id ASC", User.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Удалить пользователя по логину.
     * @param login логин.
     * @return true/false.
     */
    public boolean deleteByLogin(String login) {
        var result = false;
        var session = sf.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("DELETE FROM User WHERE login = :fLogin", User.class)
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