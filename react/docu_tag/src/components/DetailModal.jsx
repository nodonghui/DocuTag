// components/DetailModal.jsx
import { useState } from "react";
import TagBar from "./TagBar";
import { updateDocument, deleteDocument } from "../api/documentApi";

export default function DetailModal({ doc, onClose, onUpdated, onDeleted }) {
  const [title, setTitle] = useState(doc.title);
  const [content, setContent] = useState(doc.content || "");
  const [tags, setTags] = useState(doc.tags || []);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);


  const handleAddTag = (tag) => {
    if (tags.includes(tag)) return;
    setTags((prev) => [...prev, tag]);
  };

  const handleRemoveTag = (tag) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const handleSave = async () => {
    if (!title.trim()) return;
    setSaving(true);
    try {
      const id = doc.documentId;
      const updated = await updateDocument({id, title, content, tags });
      onUpdated(updated);
      onClose();
    } catch (e) {
      console.error(e);
      alert("저장에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`"${doc.title}" 문서를 삭제하시겠습니까?`)) return;
    setDeleting(true);
    try {
      await deleteDocument(doc.documentId);
      onDeleted(doc.documentId);
      onClose();
    } catch (e) {
      console.error(e);
      alert("삭제에 실패했습니다.");
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>

        <div className="modal-title">문서 상세</div>

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
          <button
            className="btn-delete"
            onClick={handleDelete}
            disabled={deleting}
          >
            {deleting ? "삭제 중..." : "삭제"}
          </button>
          <div style={{ display: "flex", gap: "8px" }}>
            <button className="btn-cancel" onClick={onClose}>취소</button>
            <button
              className="btn-confirm"
              onClick={handleSave}
              disabled={!title.trim() || saving}
            >
              {saving ? "저장 중..." : "저장"}
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}