-- =============================================
-- DADOS INICIAIS PARA TESTE DA API
-- =============================================

-- Desenvolvedoras
INSERT INTO developer (id, name, country) VALUES (1, 'Nintendo', 'Japao');
INSERT INTO developer (id, name, country) VALUES (2, 'Rockstar Games', 'Estados Unidos');
INSERT INTO developer (id, name, country) VALUES (3, 'CD Projekt Red', 'Polonia');
INSERT INTO developer (id, name, country) VALUES (4, 'FromSoftware', 'Japao');
INSERT INTO developer (id, name, country) VALUES (5, 'Ubisoft', 'Franca');

-- Plataformas
INSERT INTO platform (id, name, company) VALUES (1, 'PlayStation 5', 'Sony');
INSERT INTO platform (id, name, company) VALUES (2, 'Xbox Series X', 'Microsoft');
INSERT INTO platform (id, name, company) VALUES (3, 'Nintendo Switch', 'Nintendo');
INSERT INTO platform (id, name, company) VALUES (4, 'PC', 'Diversos');
INSERT INTO platform (id, name, company) VALUES (5, 'Steam Deck', 'Valve');

-- Jogos
INSERT INTO game (id, name, price, genre, developer_id) VALUES (1, 'The Legend of Zelda: Tears of the Kingdom', 299.90, 'ADVENTURE', 1);
INSERT INTO game (id, name, price, genre, developer_id) VALUES (2, 'Grand Theft Auto V', 149.90, 'ACTION', 2);
INSERT INTO game (id, name, price, genre, developer_id) VALUES (3, 'The Witcher 3: Wild Hunt', 79.90, 'RPG', 3);
INSERT INTO game (id, name, price, genre, developer_id) VALUES (4, 'Elden Ring', 249.90, 'RPG', 4);
INSERT INTO game (id, name, price, genre, developer_id) VALUES (5, 'Far Cry 6', 199.90, 'FPS', 5);

-- Relacao Many-to-Many: Game <-> Platform
INSERT INTO game_platforms (game_id, platforms_id) VALUES (1, 3);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (2, 1);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (2, 2);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (2, 4);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (3, 1);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (3, 2);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (3, 4);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (3, 3);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (4, 1);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (4, 2);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (4, 4);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (5, 1);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (5, 2);
INSERT INTO game_platforms (game_id, platforms_id) VALUES (5, 4);

-- Jogadores
INSERT INTO player (id, name, email) VALUES (1, 'Joao Silva', 'joao@email.com');
INSERT INTO player (id, name, email) VALUES (2, 'Maria Santos', 'maria@email.com');
INSERT INTO player (id, name, email) VALUES (3, 'Pedro Oliveira', 'pedro@email.com');
INSERT INTO player (id, name, email) VALUES (4, 'Ana Costa', 'ana@email.com');
INSERT INTO player (id, name, email) VALUES (5, 'Lucas Souza', 'lucas@email.com');

-- Perfis (One-to-One com Player)
INSERT INTO profile (id, nickname, level, player_id) VALUES (1, 'JoaoGamer', 42, 1);
INSERT INTO profile (id, nickname, level, player_id) VALUES (2, 'MariaPro', 78, 2);
INSERT INTO profile (id, nickname, level, player_id) VALUES (3, 'PedroMaster', 15, 3);
INSERT INTO profile (id, nickname, level, player_id) VALUES (4, 'AnaQueen', 99, 4);
INSERT INTO profile (id, nickname, level, player_id) VALUES (5, 'LucasNoob', 3, 5);
