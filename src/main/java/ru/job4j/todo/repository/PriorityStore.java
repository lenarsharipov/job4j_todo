package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.Collections;
import java.util.List;

/**
 * Хранилище приоритетов.
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
     * Вывести список всех приоритетов.
     * @return список приоритетов.
     */
    public List<Priority> findAll() {
        List<Priority> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    "FROM Priority p ORDER BY p.id ASC", Priority.class
            );
        } catch (Exception exception) {
            LOG.error("Exception in log", exception);
        }
        return result;
    }
}