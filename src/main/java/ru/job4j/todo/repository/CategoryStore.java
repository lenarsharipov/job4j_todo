package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.Collections;
import java.util.List;

/**
 * Хранилище категорий.
 * @author Lenar Sharipov
 * @version 1.0
 */

@ThreadSafe
@Repository
@AllArgsConstructor
public class CategoryStore {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryStore.class.getName());

    private CrudRepository crudRepository;

    /**
     * Вывести список всех категорий.
     * @return список категорий.
     */
    public List<Category> findAll() {
        List<Category> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    "FROM Category c ORDER BY c.id ASC", Category.class
            );
        } catch (Exception exception) {
            LOG.error("Exception in log", exception);
        }
        return result;
    }

}