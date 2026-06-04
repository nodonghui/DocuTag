import subprocess
from datetime import datetime
from pathlib import Path
import tempfile

CONTAINER_NAME = "docutag-mysql"
DB_USER = "root"
DB_PASSWORD = "rootpassword"
DB_NAME = "docutag"

SQL_FILE = Path("./load_data.sql")
CONTAINER_SQL_PATH = "/tmp/load_data.sql"


def log(msg):
    print(f"[{datetime.now().strftime('%H:%M:%S')}] {msg}")


DROP_SQL = """
SET foreign_key_checks = 0;
SET unique_checks = 0;
ALTER TABLE documents DROP FOREIGN KEY fk_document_user;
ALTER TABLE document_tags DROP FOREIGN KEY fk_document_tag_document;
ALTER TABLE document_tags DROP FOREIGN KEY fk_document_tag_tag;
DROP INDEX idx_user_document ON documents;
DROP INDEX idx_tags_tag_name ON tags;
"""

LOAD_SQL = """
SET foreign_key_checks = 0;
SET unique_checks = 0;
START TRANSACTION;
LOAD DATA INFILE '/var/lib/mysql-files/users.csv'
IGNORE INTO TABLE users
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\\n'
(user_id, oauth_provider, provider_id, email, password, nickname, created_at, updated_at);

LOAD DATA INFILE '/var/lib/mysql-files/tags.csv'
IGNORE INTO TABLE tags
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\\n'
(tag_id, tag_name, created_at);

LOAD DATA INFILE '/var/lib/mysql-files/documents.csv'
IGNORE INTO TABLE documents
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\\n'
(document_id, user_id, title, content, created_at, updated_at);

LOAD DATA INFILE '/var/lib/mysql-files/document_tags.csv'
IGNORE INTO TABLE document_tags
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\\n'
(document_tag_id, document_id, tag_id, created_at);
COMMIT;
"""

RESTORE_SQL = """
SET foreign_key_checks = 0;
SET unique_checks = 0;
SET innodb_parallel_read_threads = 8;
SET innodb_ddl_threads = 8;
SET innodb_sort_buffer_size = 1073741824;
SET innodb_ddl_buffer_size = 1073741824;
SET innodb_flush_log_at_trx_commit = 0;

CREATE INDEX idx_user_document ON documents(user_id, document_id DESC);
CREATE INDEX idx_tags_tag_name ON tags(tag_name);
ALTER TABLE documents
  ADD CONSTRAINT fk_document_user
  FOREIGN KEY (user_id) REFERENCES users(user_id);
ALTER TABLE document_tags
  ADD CONSTRAINT fk_document_tag_document
  FOREIGN KEY (document_id) REFERENCES documents(document_id);
ALTER TABLE document_tags
  ADD CONSTRAINT fk_document_tag_tag
  FOREIGN KEY (tag_id) REFERENCES tags(tag_id);

SET foreign_key_checks = 1;
SET unique_checks = 1;
SET innodb_flush_log_at_trx_commit = 1;
"""


def run_sql(sql: str, label: str):
    with tempfile.NamedTemporaryFile(mode='w', suffix='.sql',
                                     encoding='utf-8', delete=False) as f:
        f.write(sql)
        tmp_path = Path(f.name)

    try:
        subprocess.run([
            "docker", "cp", str(tmp_path), f"{CONTAINER_NAME}:{CONTAINER_SQL_PATH}"
        ], check=True)

        result = subprocess.run([
            "docker", "exec", CONTAINER_NAME,
            "mysql", f"-u{DB_USER}", f"-p{DB_PASSWORD}", DB_NAME,
            "-e", f"source {CONTAINER_SQL_PATH}"
        ], capture_output=True, text=True)

        if result.returncode != 0:
            raise RuntimeError(f"{label} 실패:\n{result.stderr}")

        log(f"{label} 완료")
    finally:
        tmp_path.unlink(missing_ok=True)  # 임시 파일 삭제

def run():
    # 1단계: FK/인덱스 삭제
    log("FK/인덱스 삭제 중...")
    run_sql(DROP_SQL, "FK/인덱스 삭제")

    # 2단계: 데이터 로드 (실패 시 복구)
    try:
        log("데이터 로드 중... (시간 걸림)")
        run_sql(LOAD_SQL, "데이터 로드")
    except RuntimeError as e:
        log(f"데이터 로드 실패: {e}")
        log("FK/인덱스 복구 중...")
        try:
            run_sql(RESTORE_SQL, "FK/인덱스 복구")
            log("복구 완료 — 데이터는 롤백됨")
        except RuntimeError as e2:
            log(f"복구 실패 (이미 존재할 수 있음): {e2}")
        return

    # 3단계: FK/인덱스 복구
    log("FK/인덱스 복구 중...")
    run_sql(RESTORE_SQL, "FK/인덱스 복구")
    log("전체 완료")



if __name__ == "__main__":
    run()