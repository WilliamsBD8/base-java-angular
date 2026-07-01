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

CREATE TABLE convocation_categories (
    convocation_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (convocation_id, category_id),
    CONSTRAINT fk_convocation_categories_convocation FOREIGN KEY (convocation_id) REFERENCES convocations (id),
    CONSTRAINT fk_convocation_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
