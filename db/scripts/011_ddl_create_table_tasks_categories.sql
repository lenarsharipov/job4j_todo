CREATE TABLE tasks_categories (
    id          SERIAL PRIMARY KEY,
    task_id     INT NOT NULL REFERENCES tasks(id),
    category_id INT NOT NULL REFERENCES categories(id)
);