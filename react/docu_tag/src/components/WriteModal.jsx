// components/WriteModal.jsx
// components/WriteModal.jsx
import { useState, useRef } from "react";
import ReactMarkdown from "react-markdown";
import TagBar from "./TagBar";
import { createDocument } from "../api/documentApi";
import { createDocument as aiSummarize } from "../api/GeminiApi";

const SUMMARY_MODES = [
  { label: "기본",           value: "기본" },
  { label: "서론-본론-결론",  value: "서론본론결론" },
  { label: "MECE",          value: "MECE" },
  { label: "피라미드",       value: "피라미드" },
  { label: "PAS",           value: "PAS" },
  { label: "타임라인",       value: "타임라인" },
];

export default function WriteModal({ onClose, onSubmit }) {
  const [title, setTitle]               = useState("");
  const [content, setContent]           = useState("");
  const [tags, setTags]                 = useState([]);
  const [isEditing, setIsEditing]       = useState(false);
  const [summarizing, setSummarizing]   = useState(false);
  const [showModeSelector, setShowModeSelector] = useState(false);
  const [selectedMode, setSelectedMode] = useState("기본");
  const [submitting, setSubmitting]     = useState(false);

  const textareaRef = useRef(null);

  const handleAddTag = (tag) => {
    if (tags.includes(tag)) return;
    setTags((prev) => [...prev, tag]);
  };

  const handleRemoveTag = (tag) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const handleContentClick = () => {
    setIsEditing(true);
    setTimeout(() => textareaRef.current?.focus(), 0);
  };

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
      const summarized = await aiSummarize({ content, mode: selectedMode });
      setContent(summarized);
      setIsEditing(false);
    } catch (e) {
      console.error(e);
      alert("AI 정리에 실패했습니다.");
    } finally {
      setSummarizing(false);
    }
  };

  const handleSubmit = async () => {
    if (!title.trim()) return;
    setSubmitting(true);
    try {
      await createDocument({ title, content, tags });
      onSubmit();
      setTitle("");
      setContent("");
      setTags([]);
    } catch (e) {
      console.error(e);
      alert("문서 생성에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
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

        {/* 마크다운 / textarea 토글 */}
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
          <div style={{ display: "flex", gap: "8px" }}>
            <button className="btn-cancel" onClick={handleSummarize} disabled={summarizing}>
              {summarizing ? "정리 중..." : "AI 정리"}
            </button>
            <button className="btn-cancel" onClick={onClose}>취소</button>
            <button
              className="btn-confirm"
              onClick={handleSubmit}
              disabled={!title.trim() || submitting}
            >
              {submitting ? "저장 중..." : "작성"}
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}