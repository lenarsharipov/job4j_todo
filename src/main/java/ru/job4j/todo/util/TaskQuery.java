package ru.job4j.todo.util;

/**
 * Util class Query contains all Task queries used in project.
 * @author Lenar Sharipov
 * @version 1.0
 */
public class TaskQuery {

    public static final String UPDATE_STATUS = "UPDATE Task SET done = true WHERE id = :fId";
    public static final String DELETE_TASK = "DELETE Task t";
    public static final String ORDER_BY_ID_ASC = "ORDER BY t.id ASC";
    public static final String WHERE_ID = "WHERE t.id = :fId";
    public static final String WHERE_DONE = "WHERE t.done = :fDone";
    public static final String WHERE_CREATED = "WHERE t.created >= :fCreated";
    public static final String SELECT_DISTINCT = """
            SELECT DISTINCT t
            FROM Task t
            LEFT JOIN FETCH t.priority
            LEFT JOIN FETCH t.categories
            """;

}