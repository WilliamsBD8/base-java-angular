CREATE TYPE petition_state AS ENUM (
    'PENDIENTE', 'APROBADA', 'RECHAZADA'
);

CREATE TABLE petitions (
    id BIGSERIAL PRIMARY KEY,
    convocation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    state petition_state NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_petitions_convocation FOREIGN KEY (convocation_id) REFERENCES convocations (id),
    CONSTRAINT fk_petitions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE UNIQUE INDEX uq_petitions_user_convocation
ON petitions (user_id, convocation_id)
WHERE deleted_at IS NULL;

INSERT INTO petitions (convocation_id, user_id, state, created_at, updated_at)  
VALUES
    (1, 1, 'PENDIENTE', NOW(), NOW()),
    (2, 1, 'PENDIENTE', NOW(), NOW()),
    (3, 1, 'PENDIENTE', NOW(), NOW());  