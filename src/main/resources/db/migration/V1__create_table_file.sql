create table files(
                      id  int  PRIMARY KEY AUTO_INCREMENT,
                      file_name VARCHAR(256) NOT NULL UNIQUE,
                      location  VARCHAR(256) NOT NULL,
                      status    VARCHAR(25) DEFAULT 'ACTIVE'
);