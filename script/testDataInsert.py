import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

CSV_DIR = Path(r"C:\\Users\\rdh04\\DockerImage\\data")
USER_COUNT = 10_000
TAG_COUNT = 500
DOC_COUNT = 10_000_000

NOW = datetime.now()


def random_dt():
    return (NOW - timedelta(days=random.randint(0, 365))).strftime('%Y-%m-%d %H:%M:%S')


def log(msg):
    print(f"[{datetime.now().strftime('%H:%M:%S')}] {msg}")


def generate_users():
    log("users.csv 생성 중...")
    with open(CSV_DIR / 'users.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        for i in range(1, USER_COUNT + 1):
            writer.writerow([
                i, 'LOCAL', '', f'user{i}@test.com', '',
                f'nickname_{i}', random_dt(), random_dt()
            ])
    log(f"users.csv 완료 ({USER_COUNT:,}행)")


def generate_tags():
    log("tags.csv 생성 중...")
    with open(CSV_DIR / 'tags.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        for i in range(1, TAG_COUNT + 1):
            writer.writerow([i, f'tag_{i}', random_dt()])
    log(f"tags.csv 완료 ({TAG_COUNT:,}행)")


def generate_documents():
    log("documents.csv 생성 중... (시간 걸림)")
    with open(CSV_DIR / 'documents.csv', 'w', newline='',
              buffering=1024 * 1024 * 64) as f:
        writer = csv.writer(f)
        for i in range(1, DOC_COUNT + 1):
            writer.writerow([
                i, random.randint(1, USER_COUNT),
                f'title_{i}', f'content_{i}',
                random_dt(), random_dt()
            ])
            if i % 1_000_000 == 0:
                log(f"  documents: {i:,}행")
    log(f"documents.csv 완료 ({DOC_COUNT:,}행)")


def generate_document_tags():
    log("document_tags.csv 생성 중... (시간 걸림)")
    with open(CSV_DIR / 'document_tags.csv', 'w', newline='',
              buffering=1024 * 1024 * 64) as f:
        writer = csv.writer(f)
        pk = 1
        for doc_id in range(1, DOC_COUNT + 1):
            tag_count = random.randint(1, 5)
            tags = random.sample(range(1, TAG_COUNT + 1), tag_count)
            for tag_id in tags:
                writer.writerow([pk, doc_id, tag_id, random_dt()])
                pk += 1
            if doc_id % 1_000_000 == 0:
                log(f"  document_tags: {doc_id:,}번 문서 완료")
    log(f"document_tags.csv 완료 (총 {pk:,}행)")


def main():
    CSV_DIR.mkdir(parents=True, exist_ok=True)
    generate_users()
    generate_tags()
    generate_documents()
    generate_document_tags()


if __name__ == "__main__":
    main()