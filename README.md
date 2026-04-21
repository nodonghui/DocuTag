# DocuTag
문서 저장, 검색 사이트 

문서 작성 및 관리 시스템 - AI 기반 문서 정리 및 포매팅 기능 제공

## 프로젝트 개요

DocuTag는 사용자가 문서를 작성하고 태그로 분류하여 효율적으로 관리할 수 있는 웹 기반 문서 관리 시스템입니다. AI(Gemini)를 활용하여 문서 내용을 자동으로 정리하고 포매팅할 수 있습니다.

---

## 주요 기능

### 문서 작성
- 제목 및 내용 작성
- 태그 설정 (다중 태그 지원)
- AI 기반 내용 정리 및 포매팅

### 문서 수정
- 제목 및 내용 수정
- 태그 추가/삭제
- AI를 활용한 내용 재정리

### 문서 삭제

### 문서 조회, 검색
- 사용자 문서 조회
- 무한 스크롤 페이징
- 태그 기반 검색

### 회원 기능
- 카카오 OAuth 로그인


## 기술 스택

### Frontend
- React

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

### DB 관계

users (1) ──────< (N) documents ───< (N) document_tags >──(N) tags


