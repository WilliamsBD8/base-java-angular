CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_categories_name ON categories (name) WHERE deleted_at IS NULL;

INSERT INTO categories (name, description, created_at, updated_at)
VALUES
    ('Investigación', 'Categoría de investigación', NOW(), NOW()),
    ('Bienestar', 'Categoría de bienestar', NOW(), NOW()),
    ('Académica', 'Categoría académica', NOW(), NOW()),
    ('Deportiva', 'Categoría deportiva', NOW(), NOW()),
    ('Cultural', 'Categoría cultural', NOW(), NOW());
