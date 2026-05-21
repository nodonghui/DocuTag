package docuTag.domain.document.service;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.dto.DocumentUpdateRequest;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.entity.DocumentTag;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.service.TagService;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import docuTag.global.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private TagService tagService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private User otherUser;
    private Document document;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .email("test@test.com")
                .nickname("tester")
                .oauthProvider("local")
                .build();

        otherUser = User.builder()
                .userId(99L)
                .email("other@test.com")
                .nickname("other")
                .oauthProvider("local")
                .build();

        document = Document.builder()
                .documentId(1L)
                .user(user)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
    }

    // ========== getDocuments ==========

    @Test
    @DisplayName("태그 없이 문서 목록 조회 - 성공")
    void getDocuments_noTags_success() {
        given(documentRepository.findDocumentsWithPaging(eq(1L), isNull(), isNull(), eq(21)))
                .willReturn(List.of(document));
        given(documentRepository.findByIdsWithTags(anyList(), eq(1L)))
                .willReturn(List.of(document));

        DocumentSearchResponse response = documentService.getDocuments(List.of(), null, null, 20, 1L);

        assertThat(response.getDocuments()).hasSize(1);
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("단일 태그로 문서 목록 조회 - 성공")
    void getDocuments_singleTag_success() {
        given(documentRepository.findDocumentsByTagWithPaging(eq(1L), eq("java"), isNull(), isNull(), eq(21)))
                .willReturn(List.of(document));
        given(documentRepository.findByIdsWithTags(anyList(), eq(1L)))
                .willReturn(List.of(document));

        DocumentSearchResponse response = documentService.getDocuments(List.of("java"), null, null, 20, 1L);

        assertThat(response.getDocuments()).hasSize(1);
        verify(documentRepository).findDocumentsByTagWithPaging(1L, "java", null, null, 21);
    }

    @Test
    @DisplayName("복수 태그로 문서 목록 조회 - 성공")
    void getDocuments_multipleTags_success() {
        given(documentRepository.findDocumentsByTagsWithPaging(eq(1L), eq(List.of("java", "spring")), isNull(), isNull(), eq(21)))
                .willReturn(List.of(document));
        given(documentRepository.findByIdsWithTags(anyList(), eq(1L)))
                .willReturn(List.of(document));

        DocumentSearchResponse response = documentService.getDocuments(List.of("java", "spring"), null, null, 20, 1L);

        assertThat(response.getDocuments()).hasSize(1);
        verify(documentRepository).findDocumentsByTagsWithPaging(1L, List.of("java", "spring"), null, null, 21);
    }

    @Test
    @DisplayName("문서 목록 조회 - 다음 페이지 있음")
    void getDocuments_hasNext_true() {
        List<Document> docs = new ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            docs.add(Document.builder().documentId((long) i).user(user).title("제목" + i).content("내용").build());
        }

        given(documentRepository.findDocumentsWithPaging(eq(1L), isNull(), isNull(), eq(21)))
                .willReturn(docs);
        given(documentRepository.findByIdsWithTags(anyList(), eq(1L)))
                .willReturn(docs.subList(0, 20));

        DocumentSearchResponse response = documentService.getDocuments(List.of(), null, null, 20, 1L);

        assertThat(response.isHasNext()).isTrue();
        assertThat(response.getDocuments()).hasSize(20);
    }

    @Test
    @DisplayName("문서 목록 조회 - 결과 없음")
    void getDocuments_emptyResult() {
        given(documentRepository.findDocumentsWithPaging(eq(1L), isNull(), isNull(), eq(21)))
                .willReturn(List.of());

        DocumentSearchResponse response = documentService.getDocuments(List.of(), null, null, 20, 1L);

        assertThat(response.getDocuments()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastId()).isZero();
    }

    // ========== getDocument ==========

    @Test
    @DisplayName("문서 단건 조회 - 성공")
    void getDocument_success() {
        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(document));

        DocumentDto result = documentService.getDocument(1L, 1L);

        assertThat(result.getDocumentId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("문서 단건 조회 - 존재하지 않는 문서 (404)")
    void getDocument_notFound_throws404() {
        given(documentRepository.findByIdWithTags(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocument(999L, 1L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(404));
    }

    @Test
    @DisplayName("문서 단건 조회 - 다른 사용자 접근 (403)")
    void getDocument_forbidden_throws403() {
        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(document));

        assertThatThrownBy(() -> documentService.getDocument(1L, 99L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(403));
    }

    // ========== createDocument ==========

    @Test
    @DisplayName("문서 생성 - 태그 없이 성공")
    void createDocument_noTags_success() {
        DocumentCreateRequest request = mock(DocumentCreateRequest.class);
        given(request.getTitle()).willReturn("새 제목");
        given(request.getContent()).willReturn("새 내용");
        given(request.getTags()).willReturn(null);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(documentRepository.save(any(Document.class))).willReturn(document);

        documentService.createDocument(request, 1L);

        verify(documentRepository).save(any(Document.class));
        verify(tagService, never()).findOrCreate(anyString());
    }

    @Test
    @DisplayName("문서 생성 - 태그 포함 성공")
    void createDocument_withTags_success() {
        DocumentCreateRequest request = mock(DocumentCreateRequest.class);
        given(request.getTitle()).willReturn("새 제목");
        given(request.getContent()).willReturn("새 내용");
        given(request.getTags()).willReturn(List.of("java", "spring"));

        Tag javaTag = Tag.builder().tagId(1L).tagName("java").build();
        Tag springTag = Tag.builder().tagId(2L).tagName("spring").build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(documentRepository.save(any(Document.class))).willReturn(document);
        given(tagService.findOrCreate("java")).willReturn(javaTag);
        given(tagService.findOrCreate("spring")).willReturn(springTag);

        documentService.createDocument(request, 1L);

        verify(tagService).findOrCreate("java");
        verify(tagService).findOrCreate("spring");
    }

    @Test
    @DisplayName("문서 생성 - 존재하지 않는 유저")
    void createDocument_userNotFound_throws() {
        DocumentCreateRequest request = mock(DocumentCreateRequest.class);
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.createDocument(request, 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ========== updateDocument ==========

    @Test
    @DisplayName("문서 수정 - 성공 (태그 추가/삭제)")
    void updateDocument_success() {
        Tag existingTag = Tag.builder().tagId(1L).tagName("old-tag").build();
        Tag newTag = Tag.builder().tagId(2L).tagName("new-tag").build();

        Document docWithTag = Document.builder()
                .documentId(1L)
                .user(user)
                .title("원래 제목")
                .content("원래 내용")
                .build();
        docWithTag.addTag(existingTag);

        DocumentUpdateRequest request = mock(DocumentUpdateRequest.class);
        given(request.getTitle()).willReturn("수정 제목");
        given(request.getContent()).willReturn("수정 내용");
        given(request.getTags()).willReturn(List.of("new-tag"));

        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(docWithTag));
        given(tagService.findOrCreate("new-tag")).willReturn(newTag);

        documentService.updateDocument(1L, request, 1L);

        assertThat(docWithTag.getTitle()).isEqualTo("수정 제목");
        verify(tagService).findOrCreate("new-tag");
    }

    @Test
    @DisplayName("문서 수정 - 존재하지 않는 문서 (404)")
    void updateDocument_notFound_throws404() {
        DocumentUpdateRequest request = mock(DocumentUpdateRequest.class);
        given(documentRepository.findByIdWithTags(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.updateDocument(999L, request, 1L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(404));
    }

    @Test
    @DisplayName("문서 수정 - 다른 사용자 접근 (403)")
    void updateDocument_forbidden_throws403() {
        DocumentUpdateRequest request = mock(DocumentUpdateRequest.class);
        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(document));

        assertThatThrownBy(() -> documentService.updateDocument(1L, request, 99L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(403));
    }

    // ========== deleteDocument ==========

    @Test
    @DisplayName("문서 삭제 - 성공")
    void deleteDocument_success() {
        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(document));

        documentService.deleteDocument(1L, 1L);

        verify(documentRepository).delete(document);
    }

    @Test
    @DisplayName("문서 삭제 - 존재하지 않는 문서 (404)")
    void deleteDocument_notFound_throws404() {
        given(documentRepository.findByIdWithTags(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.deleteDocument(999L, 1L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(404));
    }

    @Test
    @DisplayName("문서 삭제 - 다른 사용자 접근 (403)")
    void deleteDocument_forbidden_throws403() {
        given(documentRepository.findByIdWithTags(1L)).willReturn(Optional.of(document));

        assertThatThrownBy(() -> documentService.deleteDocument(1L, 99L))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getStatusCode()).isEqualTo(403));
    }
}
