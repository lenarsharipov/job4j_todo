package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserStoreTest implements AutoCloseable {
    private static final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private static final SessionFactory SESSION_FACTORY = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();
    private static final CrudRepository CRUD_REPOSITORY = new CrudRepository(SESSION_FACTORY);
    private static final UserStore USER_STORE = new UserStore(CRUD_REPOSITORY);
    private static User admin;
    private static User user;

    @BeforeAll
    static void init() {
        admin = USER_STORE.findAll().get(0);
        user = new User();
        user.setName("name1");
        user.setLogin("login1");
        user.setPassword("password1");
        user.setTimezone("UTC");
    }

    @AfterEach
    void clear() {
        var users = USER_STORE.findAll();
        for (var user : users) {
            USER_STORE.deleteByLogin(user.getLogin());
        }
    }

    /**
     * Get Optional of User when User saved. While saving USer gets generated ID.
     */
    @Test
    void whenCreateUserThenGetNotEmptyOptional() {
        var userOptional = USER_STORE.save(user);
        assertThat(userOptional).isNotEmpty();
        assertThat(USER_STORE.findAll()).isEqualTo(List.of(admin, userOptional.get()));
    }

    /**
     * Get empty optional if User cannot be saved.
     */
    @Test
    void whenCreateNotUniqueUserThenGetEmptyOptional() {
        var userOptional = USER_STORE.save(admin);
        assertThat(userOptional).isEmpty();
        assertThat(USER_STORE.findAll()).isEqualTo(List.of(admin));
    }

    /**
     * Get optional of User by login and password.
     */
    @Test
    void whenFindByLoginAndPasswordThenGetUser() {
        var userOptional = USER_STORE.save(user);
        assertThat(USER_STORE.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .isEqualTo(userOptional);
    }

    /**
     * Get empty optional if User cannot be found by login and password.
     */
    @Test
    void whenFindNotExistingUserByLoginAndPasswordThenGetEmptyOptional() {
        assertThat(USER_STORE.findByLoginAndPassword("login1", "password1"))
                .isEqualTo(Optional.empty());
    }

    /**
     * Get true if User deleted by login.
     */
    @Test
    void whenDeleteByLoginThenGetTrue() {
        USER_STORE.save(user);
        assertThat(USER_STORE.deleteByLogin(user.getLogin())).isTrue();
        assertThat(USER_STORE.findByLoginAndPassword(user.getLogin(), user.getPassword()))
                .isEqualTo(Optional.empty());
    }

    /**
     * Get false if User cannot be deleted by login and password.
     */
    @Test
    void whenDeleteByIncorrectLoginThenGetFalseAndEmptyOptional() {
        assertThat(USER_STORE.deleteByLogin("login1")).isFalse();
        assertThat(USER_STORE.findAll()).isEqualTo(List.of(admin));
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(REGISTRY);
    }

}