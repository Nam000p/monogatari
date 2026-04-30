-- ==========================================================
-- 1. SEED GENRES (ID: 1 to 15)
-- ==========================================================
INSERT INTO genres (id, name) VALUES
(1, 'Action'), (2, 'Fantasy'), (3, 'Mystery'), (4, 'Psychological'), (5, 'Supernatural'),
(6, 'Romance'), (7, 'Slice of Life'), (8, 'Adventure'), (9, 'Isekai'), (10, 'Sci-Fi'),
(11, 'Horror'), (12, 'Comedy'), (13, 'Drama'), (14, 'Seinen'), (15, 'Shonen');

-- ==========================================================
-- 2. SEED AUTHORS (ID: 1 to 5)
-- ==========================================================
INSERT INTO authors (id, name, bio, avatar_url) VALUES
(1, 'Sui Ishida', 'Author of Tokyo Ghoul.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/authors/ishida.jpg'),
(2, 'Kugane Maruyama', 'Author of Overlord.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/authors/maruyama.jpg'),
(3, 'Tomohito Oda', 'Author of Komi Can''t Communicate.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/authors/oda.jpg'),
(4, 'Koushi Tachibana', 'Author of Date A Live.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/authors/tachibana.jpg'),
(5, 'Hiromu Arakawa', 'Legendary author of Fullmetal Alchemist and Tsugai no Tsugumi.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/authors/arakawa.jpg');

-- ==========================================================
-- 3. SEED STORIES (ID: 1 to 5)
-- ==========================================================
INSERT INTO stories (id, title, author_id, description, cover_url, type, status, age_limit, view_count, average_rating) VALUES
(1, 'Tokyo Ghoul:re', 1, 'In modern day Tokyo, Ken Kaneki becomes a half-ghoul after a tragic encounter.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/covers/tokyoghoul.jpg', 'MANGA', 'COMPLETED', 18, 150000, 4.9),
(2, 'Overlord', 2, 'A veteran gamer is trapped in a fantasy world as his skeletal avatar.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/covers/overlord.jpg', 'LIGHT_NOVEL', 'ONGOING', 16, 120000, 4.8),
(3, 'Komi Can''t Communicate', 3, 'Komi is a beautiful girl who has extreme social anxiety.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/covers/komi.jpg', 'MANGA', 'ONGOING', 12, 85000, 4.7),
(4, 'Date A Live - Another Route', 4, 'Shido Itsuka must date beautiful spirits to save the world.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/covers/datealive.jpg', 'LIGHT_NOVEL', 'COMPLETED', 15, 65000, 4.6),
(5, 'Tsugai no Tsugumi', 5, 'Yuru lives a quiet life in a remote mountain village.', 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/covers/tsugai.jpg', 'MANGA', 'ONGOING', 15, 40000, 4.8);

-- ==========================================================
-- 4. MAP STORY-GENRE
-- ==========================================================
INSERT INTO story_genre (story_id, genre_id) VALUES
(1, 11), (1, 4), (1, 1), (1, 14), -- TG: Horror, Psychological, Action, Seinen
(2, 1), (2, 2), (2, 9), (2, 5),   -- Overlord: Action, Fantasy, Isekai, Supernatural
(3, 12), (3, 7), (3, 6),          -- Komi: Comedy, Slice of Life, Romance
(4, 6), (4, 10), (4, 2),          -- DAL: Romance, Sci-Fi, Fantasy
(5, 1), (5, 5), (5, 15);          -- Tsugai: Action, Supernatural, Shonen

-- ==========================================================
-- 5. SEED CHAPTERS (ID: 1 to 9)
-- ==========================================================
INSERT INTO chapters (id, story_id, chapter_number, title, content, is_premium) VALUES
(1, 1, 124.0, 'One line', '', FALSE),
(2, 1, 125.0, 'x', '', TRUE),
(3, 2, 11.1, 'Beginning', 'The legend of Ainz Ooal Gown begins here...', FALSE),
(4, 2, 11.2, 'Preparation for the new land', 'The Kingdom of Re-Estize faces its greatest threat...', TRUE),
(5, 3, 389.0, 'Kawai', '', FALSE),
(6, 3, 390.0, 'Kawai - Part 2', '', TRUE),
(7, 4, 1.0, 'Tohka DIET', 'The spacequake siren echoed through the city once again...', FALSE),
(8, 4, 2.0, 'Natsume RAISING', 'To save the spirit, Shido must make her fall in love...', TRUE),
(9, 5, 1.0, 'The Hidden World', '', FALSE);

-- ==========================================================
-- 6. SEED MANGA IMAGES & LN ILLUSTRATIONS
-- ==========================================================

-- Tokyo Ghoul:re Chapter 124.0 (Chapter ID: 1)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p1.png', 1),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p2.png', 2),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p3.png', 3),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p4.png', 4),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p5.png', 5),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p6.png', 6),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p7.png', 7),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p8.png', 8),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p9.png', 9),
(1, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c1_p10.png', 10);

-- Tokyo Ghoul:re Chapter 125.0 (Chapter ID: 2)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p1.png', 1),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p2.png', 2),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p3.png', 3),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p4.png', 4),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p5.png', 5),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p6.png', 6),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p7.png', 7),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p8.png', 8),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p9.png', 9),
(2, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tg/c2_p10.png', 10);

-- Overlord Vol 11.1 (Chapter ID: 3)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(3, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/overlord/illu_1.jpg', 1),
(3, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/overlord/illu_2.jpg', 2);
;

-- Overlord Vol 11.2 (Chapter ID: 4)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(4, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/overlord/illu_3.jpg', 1);

-- Komi Can't Communicate Chapter 389.0 (Chapter ID: 5)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p1.jpg', 1),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p2.jpg', 2),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p3.jpg', 3),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p4.jpg', 4),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p5.jpg', 5),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p6.jpg', 6),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p7.jpg', 7),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p8.jpg', 8),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p9.jpg', 9),
(5, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c1_p10.jpg', 10);


-- Komi Can't Communicate Chapter 390.0 (Chapter ID: 6)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p1.jpg', 1),
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p2.jpg', 2),
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p3.jpg', 3),
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p4.jpg', 4),
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p5.jpg', 5),
(6, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/komi/c2_p6.jpg', 6);

-- Date A Live Chapter 1.0 (Chapter ID: 7)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(7, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/dal/illu_1.jpg', 1),
(7, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/dal/illu_2.jpg', 2);

-- Date A Live Chapter 2.0 (Chapter ID: 8)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(8, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/dal/illu_3.jpg', 1),
(8, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/ln/dal/illu_4.jpg', 2);

-- Tsugai no Tsugumi Chapter 1.0 (Chapter ID: 9)
INSERT INTO chapter_images (chapter_id, image_url, order_number) VALUES
(9, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tsugai/c1_p1.jpg', 1),
(9, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tsugai/c1_p2.jpg', 2),
(9, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tsugai/c1_p3.jpg', 3),
(9, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tsugai/c1_p4.jpg', 4),
(9, 'https://unconfederated-fernande-tegularly.ngrok-free.dev/uploads/manga/tsugai/c1_p5.jpg', 5);