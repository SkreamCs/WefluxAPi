create table users(
                      id int PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(256) NOT NULL UNIQUE,
                      email     VARCHAR(256) NOT NULL UNIQUE,
                      password  VARCHAR(256) NOT NULL,
                      role      VARCHAR(50)  NOT NULL,
                      created  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                      updated  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                      status    VARCHAR(25) DEFAULT 'ACTIVE'
);