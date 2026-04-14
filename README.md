문자 작성, 저장 사이트

요구사항
1. 문자 작성
   - 제목 / 내용 작성
   - 태그 설정
   - ai 이용한 내용 정리 / 포매팅
      - 원하는 글 형식으로 선택
      - ai에게 추가 요청
2. 문서 수정
   - 제목 / 내용 수정
   - 태그 수정
   - ai 이용한 내용 수정
3. 문서 삭제
4. 문서 조회
   - 등록순 조회(기본), 페이징(무한 스크롤)
   - 태그 사용 검색 , 기간 검색 , 기간 오름차순 / 내림 차순
5. 회원 기능
   - 로그인
  


자바 api
문서 작성
 - DocuSave(Document docu)
 - gemiCall(AiRequest request)
문서 수정
 - DocuModify(Document docu)
문서 삭제
 - DocuDelete(int DocuId)
문서 조회
 - DocuSelectPaging(Page page)
 - DocuSearch(String KeyWord, String sort)
로그인   


DB erd
users (1) ──────< (N) documents
                         │
                         │
                         └──< (N) document_tags >──(N) tags (기준 정보)
