ALTER TABLE tasks ADD COLUMN priority_id INT REFERENCES priorities(id);
UPDATE tasks SET priority_id = (SELECT id FROM priorities WHERE name = 'Высокий');
ALTER TABLE tasks ALTER COLUMN priority_id SET NOT NULL;