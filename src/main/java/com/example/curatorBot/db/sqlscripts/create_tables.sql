CREATE TABLE IF NOT EXISTS lessons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(256),
    title VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS lesson_levels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lesson_id INT,
    difficulty INT, -- 0 - simple, 1 - medium, 2 - hard
    max_mark INT,
    gsheet_column VARCHAR(16),
    FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(256),
    gsheet_row VARCHAR(16),
    actual bit
);