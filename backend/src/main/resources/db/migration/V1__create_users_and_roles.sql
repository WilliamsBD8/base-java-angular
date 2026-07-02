CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    deleted_at  TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_roles_name ON roles (name) WHERE deleted_at IS NULL;

INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('ADMIN', 'Administrador', NOW(), NOW()),
    ('TEACHER', 'Docente', NOW(), NOW()),
    ('STUDENT', 'Estudiante', NOW(), NOW());


CREATE TYPE state_user AS ENUM (
    'ACTIVE', 'INACTIVE', 'BLOCKED'
);

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    state      state_user NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email) WHERE deleted_at IS NULL;

INSERT INTO users (name, email, password, state, created_at, updated_at)
VALUES
    ('Admin', 'admin@example.com', '$2b$10$oKUf4ASL60setVBL2VQf2.WQGFKxh/zmliayZzujfT5lUoeIUsuTi', 'ACTIVE', NOW(), NOW()),
    ('Teacher', 'teacher@example.com', '$2b$10$oKUf4ASL60setVBL2VQf2.WQGFKxh/zmliayZzujfT5lUoeIUsuTi', 'ACTIVE', NOW(), NOW()),
    ('Student', 'student@example.com', '$2b$10$oKUf4ASL60setVBL2VQf2.WQGFKxh/zmliayZzujfT5lUoeIUsuTi', 'ACTIVE', NOW(), NOW());

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3);