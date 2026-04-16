package docuTag.domain.document.repository;

import docuTag.domain.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    @Query(value = """
    SELECT d.*
    FROM documents d
    WHERE (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.created_at DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsWithPaging(
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT d.*
    FROM documents d
    JOIN document_tags dt ON d.document_id = dt.document_id
    JOIN tags t ON dt.tag_id = t.tag_id
    WHERE t.tag_name = :tag
        AND (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.created_at DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsByTagWithPaging(
            @Param("tag") String tag,
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT d.*
    FROM documents d
    WHERE EXISTS (
        SELECT 1
        FROM document_tags dt
        JOIN tags t ON dt.tag_id = t.tag_id
        WHERE dt.document_id = d.document_id
            AND t.tag_name IN (:tags)
    )
    AND (:lastId IS NULL OR d.document_id < :lastId)
    AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.created_at DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDocumentsByTagsWithPaging(
            @Param("tags") List<String> tags,
            @Param("title") String title,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );
}
