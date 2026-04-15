# DocuTag
<<<<<<< HEAD
문서 저장, 검색 사이트 
=======

문서 작성 및 관리 시스템 - AI 기반 문서 정리 및 포매팅 기능 제공

## 📋 프로젝트 개요

DocuTag는 사용자가 문서를 작성하고 태그로 분류하여 효율적으로 관리할 수 있는 웹 기반 문서 관리 시스템입니다. AI(Gemini)를 활용하여 문서 내용을 자동으로 정리하고 포매팅할 수 있습니다.

---

## ✨ 주요 기능

### 📝 문서 작성
- 제목 및 내용 작성
- 태그 설정 (다중 태그 지원)
- AI 기반 내용 정리 및 포매팅
- 다양한 글 형식 선택
- AI에게 추가 요청 가능
- tag table에 없는 단어일 경우 tag 데이터 추가

### ✏️ 문서 수정
- 제목 및 내용 수정
- 태그 추가/삭제
- AI를 활용한 내용 재정리

### 🗑️ 문서 삭제
- 소프트 삭제 방식 (복구 가능)

### 🔍 문서 조회
- 등록순 조회 (기본)
- 무한 스크롤 페이징
- 태그 기반 검색
- 기간 검색
- 정렬 (오름차순/내림차순)

### 👤 회원 기능
- 카카오 OAuth 로그인

---

## 🛠️ 기술 스택

### Frontend
- React
- CSS3

### Backend
- Java
- Spring Boot
- JPA

### Database
- MySQL

### AI
- Google Gemini API

### Authentication
- Kakao OAuth 2.0

users (1) ──────< (N) documents ───< (N) document_tags >──(N) tags

---

## 📊 데이터베이스 설계 (ERD)

### 테이블 구조

#### users (회원)
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    oauth_provider VARCHAR(20) NOT NULL,
    oauth_id VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    nickname VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE KEY unique_oauth (oauth_provider, oauth_id)
);
#### documents (문서)
CREATE TABLE documents (
    document_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
#### tags (태그)
CREATE TABLE tags (
    tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

#### document_tags (문서-태그 연결)
CREATE TABLE document_tags (
    document_tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE,
);


4/14 : 프로젝트 설계, 개발 환경 세팅, DB 테이블 생성, 기본 정보(tag) insert, api 구현
4/15 : 자바 CRUD api 구현, 프론트 구현
4/16 : 로그인 구현, 제미나이 api 세팅, 테스트
4/17 : 제미나이 기능 구현, 테스트 코드 작성
4/18 : aws 인프라 설계/조사 , 초기 세팅
4/19 : 배포 환경 세팅
4/20 : 마무리 / 통합 테스트
>>>>>>> 246883ebdc4818ae1eddcda257460554c4660c3b
