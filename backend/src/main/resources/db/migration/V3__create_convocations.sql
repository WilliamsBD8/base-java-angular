CREATE TYPE convocations_states AS ENUM (
    'BORRADOR', 'PUBLICADA', 'CERRADA'
);

CREATE TABLE convocations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    initial_date TIMESTAMP NOT NULL,
    final_date TIMESTAMP NOT NULL,
    quota INT NOT NULL,
    state convocations_states NOT NULL DEFAULT 'BORRADOR',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_convocations_user FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO convocations (user_id, name, description, initial_date, final_date, quota, state, created_at, updated_at)
VALUES
    (2, 'Convocatoria 1', 'Descripción 1', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 10, 'PUBLICADA', NOW(), NOW()),
    (2, 'Convocatoria 2', 'Descripción 2', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 10, 'PUBLICADA', NOW(), NOW()),
    (2, 'Convocatoria 3', 'Descripción 3', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 10, 'PUBLICADA', NOW(), NOW()),
    (2, 'Convocatoria 4', 'Descripción 4', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 10, 'BORRADOR', NOW(), NOW()),
    (2, 'Convocatoria 5', 'Descripción 5', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 10, 'CERRADA', NOW(), NOW());

CREATE TABLE convocation_categories (
    convocation_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (convocation_id, category_id),
    CONSTRAINT fk_convocation_categories_convocation FOREIGN KEY (convocation_id) REFERENCES convocations (id),
    CONSTRAINT fk_convocation_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

INSERT INTO convocation_categories (convocation_id, category_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 2),
    (2, 3),
    (3, 3),
    (4, 1),
    (5, 3);
