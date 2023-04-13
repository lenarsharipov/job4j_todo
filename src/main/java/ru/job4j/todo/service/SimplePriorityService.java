package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.PriorityStore;

import java.util.List;

@ThreadSafe
@AllArgsConstructor
@Service
public class SimplePriorityService implements PriorityService {

    private PriorityStore priorityStore;

    @Override
    public List<Priority> findAll() {
        return priorityStore.findAll();
    }
}