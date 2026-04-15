// components/Navbar.jsx
export default function Navbar({ search, onSearchChange, onWriteClick }) {
  return (
    <nav className="navbar">
      <span className="logo">DocuTag</span>
      <div className="search-wrap">
        <input
          className="search-input"
          placeholder="문서 제목 검색..."
          value={search}
          onChange={(e) => onSearchChange(e.target.value)}
        />
        <button className="btn-search">검색</button>
      </div>
      <button className="btn-write" onClick={onWriteClick}>+ 작성</button>
    </nav>
  );
}