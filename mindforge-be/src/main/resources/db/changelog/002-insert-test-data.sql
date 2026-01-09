--liquibase formatted sql

--changeset mindforge:insert-test-users
INSERT INTO users (username, password, role, email, first_name, last_name) VALUES
('testuser1', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'USER', 'user1@example.com', 'John', 'Doe'),
('testuser2', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'USER', 'user2@example.com', 'Jane', 'Smith'),
('admin', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'ADMIN', 'admin@example.com', 'Admin', 'User');

--changeset mindforge:insert-test-projects
INSERT INTO projects (name, description, owner_id, is_public) VALUES
('Sample Project 1', 'A demo project for testing purposes', 1, true),
('Private Project', 'This is a private project', 1, false),
('Team Project', 'Collaborative project for the team', 2, true);