package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.util.Message;
import ru.job4j.todo.util.PriorityQuery;

import java.util.Collections;
import java.util.List;

/**
 * Priority repository.
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Repository
@AllArgsConstructor
public class PriorityStore {
    private static final Logger LOG = LoggerFactory.getLogger(PriorityStore.class.getName());

    private final CrudRepository crudRepository;

    /**
     * List all priorities persisted in DB.
     * @return list of priorities.
     */
    public List<Priority> findAll() {
        List<Priority> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    PriorityQuery.SELECT_ALL, Priority.class);
        } catch (Exception exception) {
            LOG.error(Message.PRIORITIES_NOT_FOUND, exception);
        }
        return result;
    }
}