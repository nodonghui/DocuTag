package docuTag.domain.tag.repository;

import docuTag.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);


    // TagRepository.java
    @Query(value = """
    SELECT t.tag_name
    FROM tags t
    JOIN document_tags dt ON t.tag_id = dt.tag_id
    WHERE dt.document_id = :documentId
    """, nativeQuery = true)
    List<String> findTagNamesByDocumentId(@Param("documentId") Long documentId);
}
