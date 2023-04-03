package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.repository.TaskStore;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

@ThreadSafe
@AllArgsConstructor
@Service
public class SimpleTaskService implements TaskService {

    private final TaskStore taskStore;

    @Override
    public Task save(Task task) {
        return taskStore.save(task);
    }

    @Override
    public boolean update(Task task) {
        return taskStore.update(task);
    }

    @Override
    public boolean updateStatus(int id) {
        return taskStore.updateStatus(id);
    }

    @Override
    public List<Task> findAll() {
        return taskStore.findAll();
    }

    @Override
    public List<Task> findAllCompleted() {
        return taskStore.findAllCompleted();
    }

    @Override
    public List<Task> findAllNew() {
        return taskStore.findAllNew();
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskStore.getById(id);
    }

    @Override
    public boolean delete(int id) {
        return taskStore.delete(id);
    }

}