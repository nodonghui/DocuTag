// components/TagBar.jsx
import TagInputRow from "./TagInputRow";

export default function TagBar({ tags, onRemove, onAdd }) {
  return (
    <div className="tag-bar">
      <span className="tag-bar-label">태그</span>
      {tags.map((tag) => (
        <span key={tag} className="tag-chip">
          {tag}
          <button className="tag-remove" onClick={() => onRemove(tag)}>×</button>
        </span>
      ))}
      <TagInputRow onAdd={onAdd} />
    </div>
  );
}