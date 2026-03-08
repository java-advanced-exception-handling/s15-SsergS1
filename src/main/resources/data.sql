-- Insert users with specific IDs (4, 5, 6 reserved for tests)
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (5, 'Nick', 'Green', 'nick@mail.com', '{noop}Qwerty1!', 'USER');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (6, 'Nora', 'White', 'nora@mail.com', '{noop}Qwerty2@', 'USER');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (4, 'Mike', 'Brown', 'mike@mail.com', '{noop}Qwerty0#', 'ADMIN');

-- Reset sequence to continue from the next ID
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Insert states with specific IDs (5, 6, 7, 8 reserved for tests)
INSERT INTO states (id, name) VALUES (5, 'New');
INSERT INTO states (id, name) VALUES (6, 'Doing');
INSERT INTO states (id, name) VALUES (7, 'Verify');
INSERT INTO states (id, name) VALUES (8, 'Done');

-- Reset sequence to continue from the next ID
SELECT setval('states_id_seq', (SELECT MAX(id) FROM states));

-- Insert todos with specific IDs
INSERT INTO todos (id, title, created_at, owner_id) VALUES (7, 'Mike''s To-Do #1', '2023-09-16 14:00:04.810221', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (8, 'Mike''s To-Do #2', '2023-09-16 14:00:11.480271', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (9, 'Mike''s To-Do #3', '2023-09-16 14:00:16.351238', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (10, 'Nick''s To-Do #1', '2023-09-16 14:14:54.532337', 5);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (11, 'Nick''s To-Do #2', '2023-09-16 14:15:04.707176', 5);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (12, 'Nora''s To-Do #1', '2023-09-16 14:15:32.464391', 6);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (13, 'Nora''s To-Do #2', '2023-09-16 14:15:39.16246', 6);

-- Reset sequence to continue from the next ID
SELECT setval('todos_id_seq', (SELECT MAX(id) FROM todos));

-- Insert tasks with specific IDs
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (6, 'Task #2', 'LOW', 7, 5);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (5, 'Task #1', 'HIGH', 7, 8);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (7, 'Task #3', 'MEDIUM', 7, 6);

-- Reset sequence to continue from the next ID
SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));

-- Insert collaborators (junction table without ID)
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (7, 5);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (7, 6);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (10, 6);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (10, 4);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (12, 5);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (12, 4);