DROP TABLE tickets;

-- Criação da tabela de tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    creation_date DATE NOT NULL,
    update_date DATE NOT NULL
);

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(127) NOT NULL,
    email VARCHAR(127) NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    birth_date DATE,
    role VARCHAR(63),
    location VARCHAR(255)
);

-- Inserção de um usuário inicial
-- INSERT INTO users (name, email, birth_date, role, location) 
-- VALUES ('Administrador', 'CwTycUXsF1YdH..sIYtKOU4Fdw5wrewrewBS9z8m4qDxgrMDAkudY6t542f6', admin@example.com', '1980-01-01', 'Admin', 'Brasil');