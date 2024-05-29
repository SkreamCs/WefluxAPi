create table files
(
    id        int PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(256) NOT NULL UNIQUE,
    location  VARCHAR(256) NOT NULL,
    status    VARCHAR(25) DEFAULT 'ACTIVE'
);
create table users
(
    id       int PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(256) NOT NULL UNIQUE,
    email    VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    role     VARCHAR(50)  NOT NULL,
    created  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    status   VARCHAR(25)           DEFAULT 'ACTIVE'
);
CREATE TABLE events
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    file_id INT,
    status  VARCHAR(25) DEFAULT 'ACTIVE',
    CONSTRAINT uniqueEvent UNIQUE (user_id, file_id),
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_file_id
        FOREIGN KEY (file_id)
            REFERENCES files (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);