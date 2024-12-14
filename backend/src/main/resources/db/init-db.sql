-- Criação da tabela de tickets
CREATE TABLE IF NOT EXISTS tickets (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- Inserção de um ticket inicial
INSERT INTO tickets (description) VALUES ('Chamado inicial');

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(127) NOT NULL,
    email VARCHAR(127) NOT NULL UNIQUE,
    birth_date DATE,
    role VARCHAR(63),
    location VARCHAR(255)
);

-- Inserção de um usuário inicial
-- INSERT INTO users (name, email, birth_date, role, location) 
-- VALUES ('Administrador', 'admin@example.com', '1980-01-01', 'Admin', 'Brasil');