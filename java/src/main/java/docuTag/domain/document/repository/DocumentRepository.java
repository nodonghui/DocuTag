package docuTag.domain.document.repository;

import docuTag.domain.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = """
    SELECT d.*
    FROM documents d
    WHERE d.user_id = :userId
        AND (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.document_id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsWithPaging(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT d.*
    FROM documents d
    JOIN document_tags dt ON d.document_id = dt.document_id
    JOIN tags t ON dt.tag_id = t.tag_id
    WHERE d.user_id = :userId
        AND t.tag_name = :tag
        AND (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.document_id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsByTagWithPaging(
            @Param("userId") Long userId,
            @Param("tag") String tag,
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT d.*
    FROM documents d
    WHERE d.user_id = :userId
        AND EXISTS (
            SELECT 1
            FROM document_tags dt
            JOIN tags t ON dt.tag_id = t.tag_id
            WHERE dt.document_id = d.document_id
                AND t.tag_name IN (:tags)
        )
        AND (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.document_id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsByTagsWithPaging(
            @Param("userId") Long userId,
            @Param("tags") List<String> tags,
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    @Query("""
    SELECT d FROM Document d
    JOIN FETCH d.user
    LEFT JOIN FETCH d.documentTags dt
    LEFT JOIN FETCH dt.tag
    WHERE d.documentId = :id
    """)
    Optional<Document> findByIdWithTags(@Param("id") Long id);

    @Query("""
    SELECT d FROM Document d
    LEFT JOIN FETCH d.documentTags dt
    LEFT JOIN FETCH dt.tag
    WHERE d.documentId IN :ids
        AND d.user.userId = :userId
    ORDER BY d.createdAt DESC
    """)
    List<Document> findByIdsWithTags(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );
}
