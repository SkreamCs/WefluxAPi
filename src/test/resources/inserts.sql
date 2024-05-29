INSERT INTO users (username, email, password, role)
VALUES ('AdminUserTest', 'testEmail1@email.com', '9hGvCRtaqGY8jqL/52hu8EMac4fude7LDCGH+IKymnc=', 'ADMIN');

INSERT INTO users (username, email, password, role)
VALUES ('ModeratorUserTest', 'testEmail2@email.com', '9hGvCRtaqGY8jqL/52hu8EMac4fude7LDCGH+IKymnc=', 'MODERATOR');

INSERT INTO users (username, email, password, role)
VALUES ('UserUserTest', 'testEmail3@email.com', '9hGvCRtaqGY8jqL/52hu8EMac4fude7LDCGH+IKymnc=', 'USER');


INSERT INTO files (file_name, location)
VALUES ('testFile.txt', 'https://<bucketName>.s3.amazonaws.com/testFile.txt');

INSERT INTO files (file_name, location)
VALUES ('testFile2.txt', 'https://<bucketName>.s3.amazonaws.com/testFile.txt');


INSERT INTO events (user_id, file_id)
VALUES ('1', '1');
INSERT INTO events (user_id, file_id)
VALUES ('1', '2');
