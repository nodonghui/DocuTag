package docuTag.domain.tag.service;

import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public boolean isExistTagName(String tagName) {
        return tagRepository.existsByTagName(tagName);
    }

    public Tag findByTagName(String tagName) {
        return tagRepository.findByTagName(tagName)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found. tagName: " + tagName));
    }

    @Transactional
    public Tag findOrCreate(String tagName) {
        return tagRepository.findByTagName(tagName)
                .orElseGet(() -> tagRepository.save(
                        Tag.builder().tagName(tagName).build()
                ));
    }

    public List<String> findTagNamesByDocumentId(Long documentId) {
        return tagRepository.findTagNamesByDocumentId(documentId);
    }
}
