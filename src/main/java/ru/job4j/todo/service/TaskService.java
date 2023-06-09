package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<Task> save(Task task);
    boolean update(Task task);
    boolean updateStatus(int id);
    List<Task> findAll();
    List<Task> findAllCompleted(boolean flag);
    List<Task> findAllNew();
    Optional<Task> findById(int id);
    boolean delete(int id);
}