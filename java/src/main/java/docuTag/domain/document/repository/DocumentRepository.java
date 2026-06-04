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
    SELECT d.document_id
    FROM documents d
    WHERE d.user_id = :userId
        AND (:lastId IS NULL OR d.document_id < :lastId)
        AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
    ORDER BY d.document_id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Long> findDocumentIdsWithPaging(
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

    // test 전용 쿼리문
    @Query(value = """
    SELECT d.document_id
    FROM documents d
    WHERE d.user_id = :userId
      AND d.document_id < :lastId
      AND d.title LIKE CONCAT('%', :title, '%')
      AND EXISTS (
          SELECT 1
          FROM document_tags dt
          JOIN tags t ON dt.tag_id = t.tag_id
          WHERE dt.document_id = d.document_id
            AND t.tag_name IN (:tagNames)
      )
    ORDER BY d.document_id DESC
    LIMIT :pageSize
    """, nativeQuery = true)
    List<Long> findDocumentIdsByTagNamesPaging(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("title")  String title,
            @Param("tagNames") List<String> tagNames,
            @Param("pageSize") int pageSize
    );

    @Query("SELECT DISTINCT d FROM Document d "
            + "LEFT JOIN FETCH d.documentTags dt "
            + "LEFT JOIN FETCH dt.tag "
            + "WHERE d.documentId IN :documentIds "
            + "ORDER BY d.documentId DESC")
    List<Document> findAllWithTags(@Param("documentIds") List<Long> documentIds);

    @Query("SELECT DISTINCT d FROM Document d "
            + "JOIN FETCH d.documentTags dt "
            + "JOIN FETCH dt.tag "
            + "WHERE d.documentId = :documentId")
    Optional<Document> findWithTags(@Param("documentId") Long documentId);


    @Query(value = """
    SELECT
        filtered_d.document_id,
        filtered_d.user_id,
        filtered_d.title,
        filtered_d.content,
        filtered_d.created_at,
        filtered_d.updated_at,
        all_t.tag_name
    FROM (
        SELECT d.*
        FROM documents d
        WHERE d.user_id = :userId
          AND d.document_id < :lastId
          AND d.title LIKE CONCAT('%', :title, '%')
          AND EXISTS (
                SELECT 1
                FROM document_tags filter_dt
                JOIN tags filter_t ON filter_dt.tag_id = filter_t.tag_id
                WHERE filter_dt.document_id = d.document_id
                  AND filter_t.tag_name IN (:tagNames)
          )
        ORDER BY d.document_id DESC
        LIMIT :pageSize
    ) AS filtered_d
    JOIN document_tags all_dt ON filtered_d.document_id = all_dt.document_id
    JOIN tags all_t           ON all_dt.tag_id = all_t.tag_id
    ORDER BY filtered_d.document_id DESC
    """, nativeQuery = true)
    List<Object[]> findFilteredWithTags(
            @Param("userId")   Long userId,
            @Param("lastId")   Long lastId,
            @Param("title")    String title,
            @Param("tagNames") List<String> tagNames,
            @Param("pageSize")    int pageSize
    );

    @Query(value = """
    SELECT
        d.document_id,
        d.user_id,
        d.title,
        d.content,
        d.created_at,
        d.updated_at,
        GROUP_CONCAT(DISTINCT all_t.tag_name ORDER BY all_t.tag_name SEPARATOR ',') AS tag_names
    FROM (
        SELECT d.document_id
        FROM documents d
        WHERE d.user_id = :userId
          AND d.document_id < :lastId
          AND d.title LIKE CONCAT('%', :title, '%')
          AND EXISTS (
                SELECT 1
                FROM document_tags filter_dt
                JOIN tags filter_t ON filter_dt.tag_id = filter_t.tag_id
                WHERE filter_dt.document_id = d.document_id
                  AND filter_t.tag_name IN (:tagNames)
          )
        ORDER BY d.document_id DESC
        LIMIT :pageSize
    ) AS filtered
    JOIN documents d          ON filtered.document_id = d.document_id
    JOIN document_tags all_dt ON d.document_id = all_dt.document_id
    JOIN tags all_t           ON all_dt.tag_id = all_t.tag_id
    GROUP BY
        d.document_id,
        d.user_id,
        d.title,
        d.content,
        d.created_at,
        d.updated_at
    ORDER BY d.document_id DESC
    """, nativeQuery = true)
    List<Object[]> findFilteredWithTagsGrouped(
            @Param("userId")   Long userId,
            @Param("lastId")   Long lastId,
            @Param("title")    String title,
            @Param("tagNames") List<String> tagNames,
            @Param("pageSize")    int pageSize
    );


    @Query(value = """
    SELECT DISTINCT d.*
    FROM documents d
    JOIN document_tags dt ON d.document_id = dt.document_id
    JOIN tags t           ON dt.tag_id = t.tag_id
    WHERE t.tag_name IN (:tagNames)
      AND d.user_id = :userId
      AND d.document_id < :lastId
      AND d.title LIKE CONCAT('%', :title, '%')
    ORDER BY d.document_id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Document> findDistinctByTagNames(
            @Param("tagNames") List<String> tagNames,
            @Param("userId")   Long userId,
            @Param("lastId")   Long lastId,
            @Param("title")    String title,
            @Param("size")     int size
    );
}
