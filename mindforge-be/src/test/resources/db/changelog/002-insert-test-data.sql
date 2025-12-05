--liquibase formatted sql

--changeset mindforge:insert-test-users
INSERT INTO users (username, password, role) VALUES
('testuser1', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'USER'),
('testuser2', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'USER'),
('admin', '$2a$10$8K2L0Hkd1JcY8WqPzYQX9eXmBbQXJcQYjQXJcQYjQXJcQYjQXJcQY', 'ADMIN');