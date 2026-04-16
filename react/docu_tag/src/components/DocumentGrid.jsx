// components/DocumentGrid.jsx
import DocumentCard from "./DocumentCard";

export default function DocumentGrid({ docs, searchTags, onCardClick }) {
  return (
    <div className="grid">
      {docs.length === 0 ? (
        <div className="empty">검색 결과가 없습니다</div>
      ) : (
        docs.map((doc) => (
          <DocumentCard
            key={doc.id}
            doc={doc}
            highlightTags={searchTags}
            onClick={() => onCardClick(doc.documentId)}
          />
        ))
      )}
    </div>
  );
}