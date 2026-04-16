export default function Navbar({ search, onSearchChange, onWriteClick, onSearch }) {
  return (
    <nav className="navbar">
      <span className="logo">DocuTag</span>
      <div className="search-wrap">
        <input
          className="search-input"
          placeholder="문서 제목 검색..."
          value={search}
          onChange={(e) => onSearchChange(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && onSearch()} // 엔터도 검색
        />
        <button className="btn-search" onClick={onSearch}>검색</button>
      </div>
      <button className="btn-write" onClick={onWriteClick}>+ 작성</button>
    </nav>
  );
}