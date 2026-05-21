package docuTag.domain.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.service.DocumentService;
import docuTag.domain.jwt.CustomOAuth2UserService;
import docuTag.domain.jwt.CustomUserDetailsService;
import docuTag.domain.jwt.OAuth2SuccessHandler;
import docuTag.domain.jwt.SecurityUser;
import docuTag.domain.jwt.service.BlackListService;
import docuTag.domain.user.entity.User;
import docuTag.global.exception.ServiceException;
import docuTag.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    // SecurityConfig 의존성
    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private BlackListService blackListService;
    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    @MockitoBean
    private JwtUtil jwtUtil;

    private SecurityUser securityUser;
    private DocumentDto sampleDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userId(1L)
                .email("test@test.com")
                .nickname("tester")
                .oauthProvider("local")
                .build();

        securityUser = new SecurityUser(user);

        sampleDto = new DocumentDto(
                1L, "테스트 제목", "테스트 내용",
                List.of("java", "spring"),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ========== GET /api/documents ==========

    @Test
    @DisplayName("문서 목록 조회 - 200 반환")
    void getDocuments_returns200() throws Exception {
        DocumentSearchResponse response = DocumentSearchResponse.of(List.of(sampleDto), 1L, false);
        given(documentService.getDocuments(any(), any(), any(), anyInt(), anyLong()))
                .willReturn(response);

        mockMvc.perform(get("/api/documents")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents[0].documentId").value(1))
                .andExpect(jsonPath("$.documents[0].title").value("테스트 제목"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("문서 목록 조회 - 태그/제목 파라미터 포함")
    void getDocuments_withParams_returns200() throws Exception {
        DocumentSearchResponse response = DocumentSearchResponse.of(List.of(sampleDto), 1L, false);
        given(documentService.getDocuments(any(), eq("테스트"), eq(10L), eq(5), anyLong()))
                .willReturn(response);

        mockMvc.perform(get("/api/documents")
                        .param("tags", "java")
                        .param("title", "테스트")
                        .param("lastId", "10")
                        .param("size", "5")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser)))
                .andExpect(status().isOk());
    }

    // ========== GET /api/documents/{id} ==========

    @Test
    @DisplayName("문서 단건 조회 - 200 반환")
    void getDocument_returns200() throws Exception {
        given(documentService.getDocument(1L, 1L)).willReturn(sampleDto);

        mockMvc.perform(get("/api/documents/1")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(1))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.tags").isArray());
    }

    @Test
    @DisplayName("문서 단건 조회 - 없는 문서 404")
    void getDocument_notFound_returns404() throws Exception {
        given(documentService.getDocument(999L, 1L))
                .willThrow(new ServiceException(404, "Document not found: 999"));

        mockMvc.perform(get("/api/documents/999")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("문서 단건 조회 - 권한 없음 403")
    void getDocument_forbidden_returns403() throws Exception {
        given(documentService.getDocument(1L, 1L))
                .willThrow(new ServiceException(403, "Document access denied: 1"));

        mockMvc.perform(get("/api/documents/1")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser)))
                .andExpect(status().isForbidden());
    }

    // ========== POST /api/documents ==========

    @Test
    @DisplayName("문서 생성 - 201 반환")
    void createDocument_returns201() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "새 문서",
                "content", "내용",
                "tags", List.of("java")
        );

        doNothing().when(documentService).createDocument(any(), anyLong());

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("문서 생성 - 제목 없으면 400 반환")
    void createDocument_blankTitle_returns400() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "",
                "content", "내용"
        );

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("문서 생성 - 제목 200자 초과 시 400 반환")
    void createDocument_titleTooLong_returns400() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "a".repeat(201),
                "content", "내용"
        );

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // ========== PUT /api/documents/{id} ==========

    @Test
    @DisplayName("문서 수정 - 200 반환")
    void updateDocument_returns200() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "수정 제목",
                "content", "수정 내용",
                "tags", List.of("spring")
        );

        doNothing().when(documentService).updateDocument(anyLong(), any(), anyLong());

        mockMvc.perform(put("/api/documents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("문서 수정 - 권한 없음 403")
    void updateDocument_forbidden_returns403() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "수정 제목",
                "content", "수정 내용"
        );

        doThrow(new ServiceException(403, "Document access denied: 1"))
                .when(documentService).updateDocument(anyLong(), any(), anyLong());

        mockMvc.perform(put("/api/documents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ========== DELETE /api/documents/{id} ==========

    @Test
    @DisplayName("문서 삭제 - 204 반환")
    void deleteDocument_returns204() throws Exception {
        doNothing().when(documentService).deleteDocument(anyLong(), anyLong());

        mockMvc.perform(delete("/api/documents/1")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("문서 삭제 - 없는 문서 404")
    void deleteDocument_notFound_returns404() throws Exception {
        doThrow(new ServiceException(404, "Document not found: 999"))
                .when(documentService).deleteDocument(anyLong(), anyLong());

        mockMvc.perform(delete("/api/documents/999")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("문서 삭제 - 권한 없음 403")
    void deleteDocument_forbidden_returns403() throws Exception {
        doThrow(new ServiceException(403, "Document access denied: 1"))
                .when(documentService).deleteDocument(anyLong(), anyLong());

        mockMvc.perform(delete("/api/documents/1")
                        .with(SecurityMockMvcRequestPostProcessors.user(securityUser))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
