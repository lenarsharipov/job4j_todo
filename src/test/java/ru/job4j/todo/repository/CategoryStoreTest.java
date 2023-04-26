package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryStoreTest {
    private static final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private static final SessionFactory SESSION_FACTORY = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();
    private static final CrudRepository CRUD_REPOSITORY = new CrudRepository(SESSION_FACTORY);
    private static final CategoryStore CATEGORY_STORE = new CategoryStore(CRUD_REPOSITORY);

    @Test
    void whenFindAllThenGetListOfPriorities() {
        var home = new Category(1, "Home");
        var hobby = new Category(2, "Hobby");
        var leisure = new Category(3, "Leisure");
        var family = new Category(4, "Family");
        var business = new Category(5, "Business");
        var sport = new Category(6, "Sport");
        var friends = new Category(7, "Friends");

        assertThat(CATEGORY_STORE.findAll())
                .isEqualTo(List.of(home, hobby, leisure, family, business, sport, friends));
    }

}