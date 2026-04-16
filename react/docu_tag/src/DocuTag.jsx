import { useState, useEffect, useRef, useCallback } from "react";
import "./styles/DocuTag.css";

import Navbar from "./components/Navbar";
import TagBar from "./components/TagBar";
import DocumentGrid from "./components/DocumentGrid";
import WriteModal from "./components/WriteModal";
import DetailModal from "./components/DetailModal";
import { fetchDocuments, fetchDocumentById } from "./api/documentApi";

export default function DocuTag() {
  const [search, setSearch] = useState("");
  const [searchTags, setSearchTags] = useState([]);
  const [showWrite, setShowWrite] = useState(false);
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pageState, setPageState] = useState({
    lastId: null,
    size: 10,
    hasNext: true,
  });
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [loadingDetail, setLoadingDetail] = useState(false);

  const observerRef = useRef(null);
  const searchRef = useRef({ tags: [], title: null });
  const loadingRef = useRef(false);
  const isInitialized = useRef(false);

  useEffect(() => {
    searchRef.current = { tags: searchTags, title: search };
  }, [searchTags, search]);

  const loadDocuments = useCallback(async ({ tags, title, lastId, size, reset = false }) => {
    console.log("📥 loadDocuments 호출", { tags, title, lastId, size, reset });
    console.log("📄 현재 pageState", pageState);
    console.log("⏳ loadingRef", loadingRef.current);

    if (loadingRef.current) return;
    loadingRef.current = true;
    setLoading(true);
    try {
      const data = await fetchDocuments({ tags, title, lastId, size });
      setDocs((prev) => reset ? data.documents : [...prev, ...data.documents]);
      setPageState({
        lastId: data.lastId,
        hasNext: data.hasNext,
        size: size,
      });
    } catch (e) {
      console.error(e);
      setPageState((prev) => ({ ...prev, hasNext: false }));
    } finally {
      loadingRef.current = false;
      setLoading(false);

      console.log("📥 loadDocuments 호출 이후", { tags, title, lastId, size, reset });
      console.log("📄 호출이후 pageState", pageState);
      console.log("⏳ 호출이후 loadingRef", loadingRef.current);
    }
  }, []);

  // 최초 1회 전체 조회
  useEffect(() => {
    if (isInitialized.current) return;
    isInitialized.current = true;
    loadDocuments({ tags: [], title: null, lastId: null, size: 10, reset: true });
  }, []);

  // 무한 스크롤 Observer
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        const target = entries[0];
        if (target.isIntersecting && pageState.hasNext && !loadingRef.current) {
          loadDocuments({
            tags: searchRef.current.tags,
            title: searchRef.current.title?.trim() || null,
            lastId: pageState.lastId,
            size: pageState.size,
          });
        }
      },
      { threshold: 0.1 }
    );

    const el = observerRef.current;
    if (el) observer.observe(el);
    return () => observer.disconnect();
  }, [pageState]);

  // 검색 버튼 클릭
  const handleSearch = () => {
    setPageState({ lastId: null, size: 10, hasNext: true });
    loadDocuments({
      tags: searchTags,
      title: search.trim() || null,
      lastId: null,
      size: 10,
      reset: true,
    });
  };

  // 카드 클릭 → 단일 조회
  const handleCardClick = async (id) => {
    console.log("card click id : " + id)
    setLoadingDetail(true);
    try {
      const doc = await fetchDocumentById(id);
      console.log("단일 조회 문서 : " + JSON.stringify(doc));
      setSelectedDoc(doc);
    } catch (e) {
      console.error(e);
      alert("문서를 불러오지 못했습니다.");
    } finally {
      setLoadingDetail(false);
    }
  };

  const handleUpdated = async () => {
    await loadDocuments({
      tags: searchRef.current.tags,
      title: searchRef.current.title,
      lastId: null,
      size: pageState.size,
      reset: true,
    });
  };

  const handleDeleted = async () => {
    await loadDocuments({
      tags: searchRef.current.tags,
      title: searchRef.current.title,
      lastId: null,
      size: pageState.size,
      reset: true,
    });
  };

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

  return (
    <div style={{ fontFamily: "'Noto Sans KR', sans-serif", minHeight: "100vh", background: "#0f0f11", color: "#e8e6e1" }}>
      <Navbar
        search={search}
        onSearchChange={setSearch}
        onWriteClick={() => setShowWrite(true)}
        onSearch={handleSearch}
      />
      <TagBar tags={searchTags} onRemove={handleRemoveTag} onAdd={handleAddTag} />

      <main className="main">
        <div className="result-info">
          <span>{docs.length}</span>개의 문서
          {searchTags.length > 0 && <> · <span>{searchTags.join(", ")}</span> 태그 검색 중</>}
        </div>

        <DocumentGrid
          docs={docs}
          searchTags={searchTags}
          onCardClick={handleCardClick}
        />

        <div ref={observerRef} style={{ height: "10px" }} />

        {loading && <p style={{ textAlign: "center", color: "#888" }}>불러오는 중...</p>}
        {!pageState.hasNext && <p style={{ textAlign: "center", color: "#555" }}>마지막 페이지입니다.</p>}
      </main>

      {showWrite && (
        <WriteModal onClose={() => setShowWrite(false)} onSubmit={handleWrite} />
      )}

      {loadingDetail && (
        <div className="modal-overlay">
          <p style={{ color: "#888", textAlign: "center" }}>불러오는 중...</p>
        </div>
      )}

      {selectedDoc && (
        <DetailModal
          doc={selectedDoc}
          onClose={() => setSelectedDoc(null)}
          onUpdated={handleUpdated}
          onDeleted={handleDeleted}
        />
      )}
    </div>
  );
}