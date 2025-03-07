-- encoding: utf8

CREATE TABLE notification (
    id              SERIAL PRIMARY KEY,
    job_name        VARCHAR(50) NOT NULL,
    trigger_name    VARCHAR(50) NOT NULL,
    cron_schedule   VARCHAR(20) NOT NULL,
    message TEXT    NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    no_active       BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE users (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100),
    telegram        VARCHAR(100) UNIQUE,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    no_active       BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE permissions (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     VARCHAR(255)
);

CREATE TABLE roles (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE,
    description     VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id         BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id         BIGINT NOT NULL,
    role_id         BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Администратор системы с полным доступом'),
('PREACHER', 'Проповедник с доступом к управлению расписанием проповедей'),
('REGENT', 'Регент с доступом к управлению хором');

INSERT INTO permissions (name, description) VALUES 
('CREATE_NOTIFICATION', 'Создание уведомлений'),
('EDIT_NOTIFICATION', 'Редактирование уведомлений'),
('DELETE_NOTIFICATION', 'Удаление уведомлений'),
('VIEW_NOTIFICATION', 'Просмотр уведомлений'),
('MANAGE_USERS', 'Управление пользователями'),
('MANAGE_ROLES', 'Управление ролями и правами');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Initial notification for weekly message job
INSERT INTO notification (job_name, trigger_name, cron_schedule, message, created_at)
VALUES (
    'weeklyMessageJob',
    'weeklyTrigger',
    '0 0 16 ? * SAT',
    'Дорогие проповедники!

Если у кого есть готовая проповедь на ближайшее воскресенье.

То будьте добры - поделитесь драгоценными ссылками из Библии для вашей проповеди в этой группе.

Все что будет в телеграме, то и будет на экране.',
    NOW()
);
