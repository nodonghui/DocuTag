// components/DetailModal.jsx
import { useState, useRef } from "react";
import ReactMarkdown from "react-markdown";
import TagBar from "./TagBar";
import { updateDocument, deleteDocument } from "../api/documentApi";
import { createDocument } from "../api/GeminiApi";

const SUMMARY_MODES = [
  { label: "기본",          value: "기본" },
  { label: "서론-본론-결론", value: "서론본론결론" },
  { label: "MECE",         value: "MECE" },
  { label: "피라미드",      value: "피라미드" },
  { label: "PAS",          value: "PAS" },
  { label: "타임라인",      value: "타임라인" },
];

export default function DetailModal({ doc, onClose, onUpdated, onDeleted }) {
  const [title, setTitle]               = useState(doc.title);
  const [content, setContent]           = useState(doc.content || "");
  const [tags, setTags]                 = useState(doc.tags || []);
  const [saving, setSaving]             = useState(false);
  const [deleting, setDeleting]         = useState(false);
  const [summarizing, setSummarizing]   = useState(false);
  const [selectedMode, setSelectedMode] = useState("기본");
  const [showModeSelector, setShowModeSelector] = useState(false);
  const [isEditing, setIsEditing]       = useState(false); // ✅ textarea/마크다운 토글

  const textareaRef = useRef(null);

  const handleAddTag = (tag) => {
    if (tags.includes(tag)) return;
    setTags((prev) => [...prev, tag]);
  };

  const handleRemoveTag = (tag) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  // 본문 클릭 → textarea 전환 + 포커스
  const handleContentClick = () => {
    setIsEditing(true);
    setTimeout(() => textareaRef.current?.focus(), 0);
  };

  // 본문 밖 클릭 → 마크다운으로 전환
  const handleBlur = () => {
    setIsEditing(false);
  };

  const handleSummarize = () => {
    if (!content.trim()) return alert("본문 내용이 없습니다.");
    setShowModeSelector(true);
  };

  const handleConfirmSummarize = async () => {
    setShowModeSelector(false);
    setSummarizing(true);
    try {
      const summarized = await createDocument({ content, mode: selectedMode });
      setContent(summarized);
      setIsEditing(false); // 결과는 마크다운으로 표시
    } catch (e) {
      console.error(e);
      alert("AI 정리에 실패했습니다.");
    } finally {
      setSummarizing(false);
    }
  };

  const handleSave = async () => {
    if (!title.trim()) return;
    setSaving(true);
    try {
      const updated = await updateDocument({ id: doc.documentId, title, content, tags });
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

        {/* ✅ 편집 중이면 textarea, 아니면 마크다운 */}
        {isEditing ? (
          <textarea
            ref={textareaRef}
            className="modal-textarea"
            placeholder="본문 내용을 입력하세요..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            onBlur={handleBlur}
          />
        ) : (
          <div
            className={`markdown-body ${!content.trim() ? "markdown-placeholder" : ""}`}
            onClick={handleContentClick}
          >
            {content.trim()
              ? <ReactMarkdown>{content}</ReactMarkdown>
              : "본문 내용을 입력하세요..."
            }
          </div>
        )}

        <div className="modal-tag-label">태그</div>
        <TagBar tags={tags} onRemove={handleRemoveTag} onAdd={handleAddTag} />

        {/* 모드 선택 */}
        {showModeSelector && (
          <div className="mode-selector">
            <div className="mode-selector-title">정리 방식 선택</div>
            <div className="mode-selector-buttons">
              {SUMMARY_MODES.map((m) => (
                <button
                  key={m.value}
                  className={`mode-btn ${selectedMode === m.value ? "active" : ""}`}
                  onClick={() => setSelectedMode(m.value)}
                >
                  {m.label}
                </button>
              ))}
            </div>
            <div className="mode-selector-actions">
              <button className="btn-cancel" onClick={() => setShowModeSelector(false)}>취소</button>
              <button className="btn-confirm" onClick={handleConfirmSummarize}>정리 시작</button>
            </div>
          </div>
        )}

        <div className="modal-actions">
          <button className="btn-delete" onClick={handleDelete} disabled={deleting}>
            {deleting ? "삭제 중..." : "삭제"}
          </button>
          <div style={{ display: "flex", gap: "8px" }}>
            <button className="btn-cancel" onClick={handleSummarize} disabled={summarizing}>
              {summarizing ? "정리 중..." : "AI 정리"}
            </button>
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