import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./styles/login.css";

import { login } from "./api/JwtApi";

const REACT_APP_API_BASE_URL = `${process.env.REACT_APP_API_URL}`;
const BACKEND_LOGIN_URL = REACT_APP_API_BASE_URL + `/oauth2/authorization/kakao`;
const GOOGLE_LOGIN_URL = REACT_APP_API_BASE_URL + `/oauth2/authorization/google`;

export default function LoginPage() {
  const [hovered, setHovered] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");

  const [loginError, setLoginError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleKakaoLogin = () => {
    window.location.href = BACKEND_LOGIN_URL;
  };

  const handleGoogleLogin = () => {
    window.location.href = GOOGLE_LOGIN_URL;
  };

  const handleLogin = async () => {
    if (!userId || !password) return;
    setLoading(true);
    setLoginError("");
    try {
        await login({ email: userId, password });
        window.location.href = "/";
      } catch (err) {
        setLoginError(err.message);
      } finally {
        setLoading(false);
    }
};

  return (
    <div className="login-container">
      <div className="bg-circle bg-circle-1" />
      <div className="bg-circle bg-circle-2" />

      <div className="login-card">
        {/* 로고 */}
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
        <p className="login-subtitle">DocuTag에 오신 것을 환영합니다</p>

        {/* 아이디 / 비밀번호 입력 */}
        <div className="input-group">
          <div className="input-wrapper">
            <span className="input-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
            </span>
            <input
              className="input-field"
              type="text"
              placeholder="아이디"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
            />
          </div>

          <div className="input-wrapper">
            <span className="input-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </span>
            <input
              className="input-field"
              type={showPassword ? "text" : "password"}
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
            />
            <button
              className="password-toggle"
              onClick={() => setShowPassword(!showPassword)}
              tabIndex={-1}
              type="button"
            >
              {showPassword ? (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                  <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              ) : (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              )}
            </button>
          </div>
        </div>

        {/* 로그인 버튼 */}
        <button
          className="login-btn"
          onClick={handleLogin}
          disabled={!userId || !password || loading}
        >
          {loading ? "로그인 중..." : "로그인"}
        </button>

        {/* 로그인 버튼 */}
        {loginError && (
          <div className="login-error">{loginError}</div>
        )}

        {/* 하단 링크: 회원가입 / 아이디 찾기 / 비밀번호 찾기 */}
        <div className="auth-links">
          <button className="auth-link-btn" onClick={() => navigate("/signup")}>
            회원가입
          </button>
          <span className="auth-link-sep">|</span>
          <button className="auth-link-btn" onClick={() => console.log("아이디 찾기")}>
            아이디 찾기
          </button>
          <span className="auth-link-sep">|</span>
          <button className="auth-link-btn" onClick={() => console.log("비밀번호 찾기")}>
            비밀번호 찾기
          </button>
        </div>

        {/* 구분선 */}
        <div className="divider">또는</div>

        {/* 카카오 로그인 */}
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
        
        {/* 구글 로그인 */}
        <button
          className="google-btn"
          onClick={handleGoogleLogin}
>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" style={{ flexShrink: 0 }}>
            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
          </svg>
          Google로 로그인
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