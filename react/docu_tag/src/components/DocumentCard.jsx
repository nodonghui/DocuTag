// components/DocumentCard.jsx
export default function DocumentCard({ doc, highlightTags }) {
  return (
    <div className="card">
      <div className="card-title">{doc.title}</div>
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