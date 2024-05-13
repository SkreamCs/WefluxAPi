CREATE TABLE events(
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       user_id INT,
                       file_id INT,
                       status    VARCHAR(25) DEFAULT 'ACTIVE',
                       CONSTRAINT uniqueEvent UNIQUE(user_id, file_id),
                       CONSTRAINT fk_user_id
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,
                       CONSTRAINT fk_file_id
                           FOREIGN KEY (file_id)
                               REFERENCES files(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE
);