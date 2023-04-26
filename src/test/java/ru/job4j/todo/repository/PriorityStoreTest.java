package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Priority;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PriorityStoreTest {
    private static final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private static final SessionFactory SESSION_FACTORY = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();
    private static final CrudRepository CRUD_REPOSITORY = new CrudRepository(SESSION_FACTORY);
    private static final PriorityStore PRIORITY_STORE = new PriorityStore(CRUD_REPOSITORY);

    @Test
    void whenFindAllThenGetListOfPriorities() {
        var veryHigh = new Priority(1, "Very High", 1);
        var high = new Priority(2, "High", 2);
        var normal = new Priority(3, "Normal", 3);
        var low = new Priority(4, "Low", 4);
        var veryLow = new Priority(5, "Very Low", 5);
        assertThat(PRIORITY_STORE.findAll())
                .isEqualTo(List.of(veryHigh, high, normal, low, veryLow));
    }

}