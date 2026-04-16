// components/WriteModal.jsx
import { useState } from "react";
import TagBar from "./TagBar";
import { createDocument } from "../api/documentApi";

export default function WriteModal({ onClose, onSubmit }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [tags, setTags] = useState([]);

  const handleAddTag = (tag) => {
    if (tags.includes(tag)) return;
    setTags((prev) => [...prev, tag]);
  };

  const handleRemoveTag = (tag) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const handleSubmit = async () => {
    if (!title.trim()) return;

    try {
      await createDocument({ title, content, tags });
      console.log("api 전송 성공");
      onSubmit({ title, content, tags }); // 성공 시 부모에 알림
      console.log("모달 닫기 성공");
    } catch (e) {
      console.error(e);
      console.log("모달 create api 에러 발생");
    }

    setTitle("");
    setContent("");
    setTags([]);
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

        <textarea
          className="modal-textarea"
          placeholder="본문 내용을 입력하세요..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />

        <div className="modal-tag-label">태그</div>
        <TagBar tags={tags} onRemove={handleRemoveTag} onAdd={handleAddTag} />

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