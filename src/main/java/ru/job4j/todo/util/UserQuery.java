package ru.job4j.todo.util;

/**
 * Util class Query contains all User queries used in project.
 * @author Lenar Sharipov
 * @version 1.0
 */
public class UserQuery {
    public static final String FILTER_BY_LOGIN_PASSWORD =
            "FROM User u WHERE u.login = :fLogin AND u.password = :fPassword";
    public static final String SELECT_ALL = "FROM User u ORDER BY u.id ASC";
    public static final String DELETE_BY_LOGIN = "DELETE FROM User u WHERE u.login = :fLogin";
}
