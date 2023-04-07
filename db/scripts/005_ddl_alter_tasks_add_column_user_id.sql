ALTER TABLE tasks ADD COLUMN user_id INT REFERENCES users(id);
UPDATE tasks SET user_id = 1 WHERE user_id is null;
ALTER TABLE tasks ALTER user_id SET DEFAULT 1;