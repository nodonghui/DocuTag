// Navbar.jsx 수정 - 로고 클릭 시 이동
export default function Navbar({ search, onSearchChange, onWriteClick, onSearch }) {
    return (
        <nav className="navbar">
            <span
                className="logo"
                style={{ cursor: "pointer" }}
                onClick={() => window.location.href = "http://localhost:3000/"}
            >
                DocuTag
            </span>
            <div className="search-wrap">
                <input
                    className="search-input"
                    placeholder="문서 제목 검색..."
                    value={search}
                    onChange={(e) => onSearchChange(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && onSearch()}
                />
                <button className="btn-search" onClick={onSearch}>검색</button>
            </div>
            <button className="btn-write" onClick={onWriteClick}>+ 작성</button>
        </nav>
    );
}