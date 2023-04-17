package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.util.CategoryQuery;
import ru.job4j.todo.util.Message;

import java.util.Collections;
import java.util.List;

/**
 * Category repository.
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
     * List all persisted categories.
     * @return list of categories.
     */
    public List<Category> findAll() {
        List<Category> result = Collections.emptyList();
        try {
            result = crudRepository.query(
                    CategoryQuery.SELECT_ALL, Category.class
            );
        } catch (Exception exception) {
            LOG.error(Message.CATEGORIES_NOT_FOUND, exception);
        }
        return result;
    }

}