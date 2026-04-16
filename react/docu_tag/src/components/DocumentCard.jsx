// components/DocumentCard.jsx
export default function DocumentCard({ doc, highlightTags, onClick }) {
  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
  };

  return (
    <div className="card" onClick={onClick} style={{ cursor: "pointer" }}>
      <div className="card-header">
        <div className="card-title">{doc.title}</div>
        <div className="card-date">{formatDate(doc.createdAt)}</div>
      </div>
      <div className="card-tags">
        {doc.tags.map((tag) => (
          <span
            key={tag}
            className={`card-tag${highlightTags.includes(tag) ? " highlight" : ""}`}
          >
            {tag}
          </span>
        ))}
      </div>
    </div>
  );
}