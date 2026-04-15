package docuTag.domain.tag.service;

import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
