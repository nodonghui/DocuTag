package docuTag.domain.document.entity;


import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.user.entity.User;
import docuTag.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_document_user"))
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentTag> documentTags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Document from(DocumentCreateRequest request, User user) {
        return Document.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
    }

    // 비즈니스 메서드
    public void updateDocument(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addTag(Tag tag) {
        DocumentTag documentTag = DocumentTag.builder()
                .document(this)
                .tag(tag)
                .build();
        this.documentTags.add(documentTag);
    }

    public void removeTag(Tag tag) {
        this.documentTags.removeIf(dt -> dt.getTag().equals(tag));
    }

    public void clearTags() {
        this.documentTags.clear();
    }
}
