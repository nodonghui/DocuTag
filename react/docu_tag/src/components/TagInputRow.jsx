// components/TagInputRow.jsx
import { useState } from "react";

export default function TagInputRow({ onAdd }) {
  const [showInput, setShowInput] = useState(false);
  const [value, setValue] = useState("");

  const handleAdd = () => {
    const trimmed = value.trim();
    if (!trimmed) return;
    onAdd(trimmed);
    setValue("");
    setShowInput(false);
  };

  return showInput ? (
    <div className="tag-input-wrap">
      <input
        className="tag-input"
        placeholder="태그 입력..."
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && handleAdd()}
        autoFocus
      />
      <button className="tag-input-confirm" onClick={handleAdd}>추가</button>
      <button className="tag-input-cancel" onClick={() => { setShowInput(false); setValue(""); }}>✕</button>
    </div>
  ) : (
    <button className="tag-add-btn" onClick={() => setShowInput(true)}>+ 태그</button>
  );
}