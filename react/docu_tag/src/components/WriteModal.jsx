// components/WriteModal.jsx
import { useState } from "react";

export default function WriteModal({ allTags, onClose, onSubmit }) {
  const [title, setTitle] = useState("");
  const [selectedTags, setSelectedTags] = useState([]);

  const toggleTag = (tag) =>
    setSelectedTags((prev) =>
      prev.includes(tag) ? prev.filter((t) => t !== tag) : [...prev, tag]
    );

  const handleSubmit = () => {
    if (!title.trim()) return;
    onSubmit({ title, tags: selectedTags });
    setTitle("");
    setSelectedTags([]);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-title">새 문서 작성</div>
        <input
          className="modal-input"
          placeholder="문서 제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <div className="modal-tag-label">태그 선택</div>
        <div className="modal-tags">
          {allTags.map((tag) => (
            <button
              key={tag}
              className={`modal-tag-btn${selectedTags.includes(tag) ? " selected" : ""}`}
              onClick={() => toggleTag(tag)}
            >
              {tag}
            </button>
          ))}
        </div>
        <div className="modal-actions">
          <button className="btn-cancel" onClick={onClose}>취소</button>
          <button className="btn-confirm" onClick={handleSubmit} disabled={!title.trim()}>
            작성
          </button>
        </div>
      </div>
    </div>
  );
}