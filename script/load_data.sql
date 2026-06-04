SET foreign_key_checks = 0;
SET unique_checks = 0;

ALTER TABLE documents DROP FOREIGN KEY fk_document_user;
ALTER TABLE document_tags DROP FOREIGN KEY fk_document_tag_document;
ALTER TABLE document_tags DROP FOREIGN KEY fk_document_tag_tag;

DROP INDEX idx_user_document ON documents;
DROP INDEX idx_tags_tag_name ON tags;

/*
START TRANSACTION;
LOAD DATA INFILE '/var/lib/mysql-files/users.csv'
IGNORE
INTO TABLE users
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(user_id, oauth_provider, provider_id, email, password, nickname, created_at, updated_at);

LOAD DATA INFILE '/var/lib/mysql-files/tags.csv'
IGNORE
INTO TABLE tags
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(tag_id, tag_name, created_at);

LOAD DATA INFILE '/var/lib/mysql-files/documents.csv'
IGNORE
INTO TABLE documents
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(document_id, user_id, title, content, created_at, updated_at);

LOAD DATA INFILE '/var/lib/mysql-files/document_tags.csv'
IGNORE
INTO TABLE document_tags
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(document_tag_id, document_id, tag_id, created_at);
COMMIT;


CREATE INDEX idx_user_document ON documents(user_id, document_id);
CREATE INDEX idx_tags_tag_name ON tags(tag_name);

-- 2. FK 재생성
ALTER TABLE documents
  ADD CONSTRAINT fk_document_user
  FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE document_tags
  ADD CONSTRAINT fk_document_tag_document
  FOREIGN KEY (document_id) REFERENCES documents(id);

ALTER TABLE document_tags
  ADD CONSTRAINT fk_document_tag_tag
  FOREIGN KEY (tag_id) REFERENCES tags(id);

*/
SET foreign_key_checks = 1;
SET unique_checks = 1;