package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private static final Logger LOG = LoggerFactory.getLogger(UserStore.class.getName());

    private final CrudRepository crudRepository;

    /**
     * Добавить нового пользователя.
     * @param user пользователь.
     * @return Optional<User>.
     */
    public Optional<User> save(User user) {
        Optional<User> result = Optional.empty();
        try {
            crudRepository.run(session -> session.persist(user));
            result = Optional.of(user);
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
        }
        return result;
    }

    /**
     * Вывести пользователя, найденного по логину и паролю.
     * @param login логин.
     * @param password пароль.
     * @return Optional<User>.
     */
    public Optional<User> findByLoginAndPassword(String login, String password) {
        Optional<User> result = Optional.empty();
        try {
            result = crudRepository.optional(
                    "FROM User WHERE login = :fLogin AND password = :fPassword",
                    User.class, Map.of(
                            "fLogin", login,
                            "fPassword", password)
            );
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
        }
        return result;
    }

    /**
     * Вывести всех пользователей.
     * @return список пользователей.
     */
    public List<User> findAll() {
        List<User> result = Collections.emptyList();
        try {
            result = crudRepository.query("FROM User ORDER BY id ASC", User.class);
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
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
        try {
            result = crudRepository.isExecuted(
                    "DELETE FROM User WHERE login = :fLogin",
                    Map.of("fLogin", login));
        } catch (Exception exception) {
            LOG.error("Exception in log example", exception);
        }
        return result;
    }

}