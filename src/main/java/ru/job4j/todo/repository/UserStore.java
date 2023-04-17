package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;
import ru.job4j.todo.util.Key;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.UserQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User Repository.
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Repository
@AllArgsConstructor
public class UserStore {

    private static final Logger LOG = LoggerFactory.getLogger(UserStore.class.getName());

    private final CrudRepository crudRepository;

    /**
     * Save new user in DB.
     * @param user new user.
     * @return Optional<User>.
     */
    public Optional<User> save(User user) {
        Optional<User> result = Optional.empty();
        try {
            crudRepository.run(session -> session.persist(user));
            result = Optional.of(user);
        } catch (Exception exception) {
            LOG.error(Message.USER_NOT_SAVED, exception);
        }
        return result;
    }

    /**
     * Get user found by login and password.
     * @param login login.
     * @param password password.
     * @return Optional<User>.
     */
    public Optional<User> findByLoginAndPassword(String login, String password) {
        Optional<User> result = Optional.empty();
        try {
            result = crudRepository.optional(
                    UserQuery.FILTER_BY_LOGIN_PASSWORD, User.class,
                    Map.of(Key.F_LOGIN, login,
                           Key.F_PASSWORD, password)
            );
        } catch (Exception exception) {
            LOG.error(Message.NOT_UNIQUE_LOGIN, exception);
        }
        return result;
    }

    /**
     * List all persisted users.
     * @return list of users.
     */
    public List<User> findAll() {
        List<User> result = Collections.emptyList();
        try {
            result = crudRepository.query(UserQuery.SELECT_ALL, User.class);
        } catch (Exception exception) {
            LOG.error(Message.USERS_NOT_FOUND, exception);
        }
        return result;
    }

    /**
     * Delete user by specified login.
     * @param login login.
     * @return true/false.
     */
    public boolean deleteByLogin(String login) {
        var result = false;
        try {
            result = crudRepository.isExecuted(
                    UserQuery.DELETE_BY_LOGIN,
                    Map.of(Key.F_LOGIN, login));
        } catch (Exception exception) {
            LOG.error(Message.USER_NOT_DELETED, exception);
        }
        return result;
    }

}