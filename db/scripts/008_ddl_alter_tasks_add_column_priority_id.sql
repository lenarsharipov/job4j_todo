ALTER TABLE tasks ADD COLUMN priority_id int REFERENCES priorities(id);
UPDATE tasks SET priority_id = (SELECT id FROM priorities WHERE name = 'urgently');