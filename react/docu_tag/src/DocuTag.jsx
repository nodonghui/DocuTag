// DocuTag.jsx
import { useState } from "react";
import "./styles/DocuTag.css";

import Navbar from "./components/Navbar";
import TagBar from "./components/TagBar";
import DocumentGrid from "./components/DocumentGrid";
import WriteModal from "./components/WriteModal";

const SAMPLE_DOCS = [
  { id: 1, title: "Spring Boot 시작하기", tags: ["Spring", "Java"] },
  { id: 2, title: "React 훅 완전 정복", tags: ["React", "TypeScript"] },
  { id: 3, title: "JPA 연관관계 매핑", tags: ["JPA", "Java", "SQL"] },
  { id: 4, title: "Docker 컨테이너 배포", tags: ["Docker", "AWS"] },
  { id: 5, title: "Redis 캐싱 전략", tags: ["Redis", "Spring"] },
  { id: 6, title: "커서 기반 페이징 구현", tags: ["SQL", "Spring", "JPA"] },
  { id: 7, title: "Kubernetes 오케스트레이션", tags: ["Kubernetes", "Docker"] },
  { id: 8, title: "TypeScript 제네릭 활용", tags: ["TypeScript", "React"] },
];

export default function DocuTag() {
  const [search, setSearch] = useState("");
  const [searchTags, setSearchTags] = useState([]);
  const [showWrite, setShowWrite] = useState(false);
  const [docs, setDocs] = useState(SAMPLE_DOCS);

  const handleAddTag = (tag) => {
    if (searchTags.includes(tag)) return;
    setSearchTags((prev) => [...prev, tag]);
  };

  const handleRemoveTag = (tag) => {
    setSearchTags((prev) => prev.filter((t) => t !== tag));
  };

  const handleWrite = ({ title, tags }) => {
    setDocs((prev) => [{ id: Date.now(), title, tags }, ...prev]);
    setShowWrite(false);
  };


  const allTags = [...new Set(docs.flatMap((d) => d.tags))];

  return (
    <div style={{ fontFamily: "'Noto Sans KR', sans-serif", minHeight: "100vh", background: "#0f0f11", color: "#e8e6e1" }}>
      <Navbar search={search} onSearchChange={setSearch} onWriteClick={() => setShowWrite(true)} />
      <TagBar tags={searchTags} onRemove={handleRemoveTag} onAdd={handleAddTag} />

      <main className="main">
        <div className="result-info">
          <span>{docs.length}</span>개의 문서
          {searchTags.length > 0 && <> · <span>{searchTags.join(", ")}</span> 태그 검색 중</>}
        </div>
        <DocumentGrid docs = {docs} searchTags={searchTags} />
      </main>

      {showWrite && (
        <WriteModal allTags={allTags} onClose={() => setShowWrite(false)} onSubmit={handleWrite} />
      )}
    </div>
  );
}