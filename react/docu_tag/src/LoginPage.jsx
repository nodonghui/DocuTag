import { useState } from "react";
import "./styles/login.css";
const REACT_APP_API_BASE_URL=`${process.env.REACT_APP_API_URL}`
const BACKEND_LOGIN_URL = REACT_APP_API_BASE_URL+`/api/auth/kakao/login`;

export default function LoginPage() {
  const [hovered, setHovered] = useState(false);

  const handleKakaoLogin = () => {
    console.log("login url : " + BACKEND_LOGIN_URL);
    window.location.href = BACKEND_LOGIN_URL;
  };

  return (
    <div className="login-container">
      <div className="bg-circle bg-circle-1" />
      <div className="bg-circle bg-circle-2" />

      <div className="login-card">
        <div className="logo-area">
          <div className="logo-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <rect width="28" height="28" rx="8" fill="#111" />
              <path d="M7 8h14M7 14h9M7 20h12" stroke="#fff" strokeWidth="2" strokeLinecap="round" />
            </svg>
          </div>
          <span className="logo-text">DocuTag</span>
        </div>

        <h1 className="login-title">시작하기</h1>
        <p className="login-subtitle">카카오 계정으로 간편하게 로그인하세요</p>

        <button
          className={`kakao-btn ${hovered ? "kakao-btn-hover" : ""}`}
          onMouseEnter={() => setHovered(true)}
          onMouseLeave={() => setHovered(false)}
          onClick={handleKakaoLogin}
        >
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" style={{ flexShrink: 0 }}>
            <path
              fillRule="evenodd"
              clipRule="evenodd"
              d="M10 3C6.134 3 3 5.462 3 8.5c0 1.928 1.13 3.627 2.85 4.686l-.726 2.657a.25.25 0 0 0 .374.277L8.74 14.1c.406.058.822.088 1.26.088 3.866 0 7-2.462 7-5.5S13.866 3 10 3z"
              fill="#191919"
            />
          </svg>
          카카오로 로그인
        </button>

        <p className="login-terms">
          로그인 시{" "}
          <span className="login-link">서비스 이용약관</span> 및{" "}
          <span className="login-link">개인정보 처리방침</span>에 동의하게 됩니다.
        </p>
      </div>
    </div>
  );
}