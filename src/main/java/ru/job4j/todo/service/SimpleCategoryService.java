package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.CategoryStore;

import java.util.List;

@ThreadSafe
@AllArgsConstructor
@Service
public class SimpleCategoryService implements CategoryService {

    private CategoryStore categoryStore;

    @Override
    public List<Category> findAll() {
        return categoryStore.findAll();
    }
}