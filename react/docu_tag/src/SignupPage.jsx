import { useState, useCallback } from "react";
import "./styles/login.css";
import { signup } from "./api/JwtApi"; // ← 추가

const REACT_APP_API_BASE_URL = `${process.env.REACT_APP_API_URL}`;

function EyeIcon({ open }) {
  return open ? (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
      <line x1="1" y1="1" x2="23" y2="23"/>
    </svg>
  ) : (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
      <circle cx="12" cy="12" r="3"/>
    </svg>
  );
}

function InputField({ label, icon, hint, hintType, children }) {
  return (
    <div className="input-field-group">
      <label className="input-label">{label}</label>
      <div className="input-wrapper">
        <span className="input-icon">{icon}</span>
        {children}
      </div>
      {hint && <span className={`input-hint ${hintType || ""}`}>{hint}</span>}
    </div>
  );
}

function getPasswordStrength(pw) {
  if (!pw) return 0;
  let score = 0;
  if (pw.length >= 8) score++;
  if (/[A-Za-z]/.test(pw) && /[0-9]/.test(pw)) score++;
  if (/[^A-Za-z0-9]/.test(pw)) score++;
  return score; // 1=weak 2=medium 3=strong
}

const strengthLabel = ["", "약함", "보통", "강함"];
const strengthClass = ["", "weak", "medium", "strong"];

export default function SignupPage() {
  const [form, setForm] = useState({
    email: "",
    nickname: "",
    password: "",
    passwordConfirm: "",
  });
  const [showPw, setShowPw]         = useState(false);
  const [showPwC, setShowPwC]       = useState(false);
  const [touched, setTouched]       = useState({});
  const [loading, setLoading]       = useState(false);
  const [serverError, setServerError] = useState("");

  const set = (key) => (e) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));
  const touch = (key) => () =>
    setTouched((t) => ({ ...t, [key]: true }));

  /* ── 유효성 ── */
  const emailValid    = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email);
  const nicknameValid = form.nickname.trim().length >= 2 && form.nickname.trim().length <= 20;
  const pwStrength    = getPasswordStrength(form.password);
  const pwValid       = form.password.length >= 8;
  const pwMatch       = form.password === form.passwordConfirm && form.passwordConfirm !== "";
  const canSubmit     = emailValid && nicknameValid && pwValid && pwMatch;

  /* ── 힌트 메시지 ── */
  const emailHint = touched.email
    ? emailValid ? "사용 가능한 이메일입니다" : "올바른 이메일 형식을 입력해주세요"
    : "";
  const nicknameHint = touched.nickname
    ? nicknameValid ? "사용 가능한 닉네임입니다" : "닉네임은 2~20자로 입력해주세요"
    : "로그인 후 서비스에서 사용될 이름입니다";
  const pwHint = touched.password
    ? pwValid ? "" : "비밀번호는 8자 이상이어야 합니다"
    : "";
  const pwConfirmHint = touched.passwordConfirm
    ? pwMatch ? "비밀번호가 일치합니다" : "비밀번호가 일치하지 않습니다"
    : "";

  /* ── 제출 ── */
  const handleSubmit = useCallback(async () => {
    if (!canSubmit) return;
    setLoading(true);
    setServerError("");
    try {
        await signup({
        email:    form.email,
        password: form.password,
        nickname: form.nickname,
        });
        window.location.href = "/";
    } catch (err) {
        setServerError(err.message);
    } finally {
        setLoading(false);
    }
  }, [canSubmit, form]);

  const mailIcon = (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
      <polyline points="22,6 12,13 2,6"/>
    </svg>
  );
  const userIcon = (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
      <circle cx="12" cy="7" r="4"/>
    </svg>
  );
  const lockIcon = (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
      <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
    </svg>
  );

  return (
    <div className="login-container">
      <div className="bg-circle bg-circle-1" />
      <div className="bg-circle bg-circle-2" />

      <div className="signup-card">
        {/* 뒤로 가기 */}
        <button className="back-btn" onClick={() => window.location.href = "/"}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          로그인으로 돌아가기
        </button>

        {/* 로고 */}
        <div className="logo-area">
          <div className="logo-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <rect width="28" height="28" rx="8" fill="#111"/>
              <path d="M7 8h14M7 14h9M7 20h12" stroke="#fff" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </div>
          <span className="logo-text">DocuTag</span>
        </div>

        <h1 className="login-title">회원가입</h1>
        <p className="login-subtitle">아래 정보를 입력해 계정을 만드세요</p>

        {/* 이메일 */}
        <InputField
          label="이메일 (아이디)"
          icon={mailIcon}
          hint={emailHint}
          hintType={touched.email ? (emailValid ? "success" : "error") : ""}
        >
          <input
            className="input-field"
            type="email"
            placeholder="example@email.com"
            value={form.email}
            onChange={set("email")}
            onBlur={touch("email")}
          />
        </InputField>

        {/* 닉네임 */}
        <InputField
          label="이름 (닉네임)"
          icon={userIcon}
          hint={nicknameHint}
          hintType={touched.nickname ? (nicknameValid ? "success" : "error") : ""}
        >
          <input
            className="input-field"
            type="text"
            placeholder="홍길동"
            value={form.nickname}
            onChange={set("nickname")}
            onBlur={touch("nickname")}
          />
        </InputField>

        {/* 비밀번호 */}
        <InputField
          label="비밀번호"
          icon={lockIcon}
          hint={pwHint}
          hintType={touched.password && !pwValid ? "error" : ""}
        >
          <input
            className="input-field"
            type={showPw ? "text" : "password"}
            placeholder="8자 이상 입력"
            value={form.password}
            onChange={set("password")}
            onBlur={touch("password")}
          />
          <button className="password-toggle" onClick={() => setShowPw(!showPw)} type="button" tabIndex={-1}>
            <EyeIcon open={showPw} />
          </button>
        </InputField>

        {/* 비밀번호 강도 바 */}
        {form.password && (
          <div>
            <div className="password-strength">
              {[1, 2, 3].map((i) => (
                <div
                  key={i}
                  className={`strength-bar ${i <= pwStrength ? strengthClass[pwStrength] : ""}`}
                />
              ))}
            </div>
            <span className={`input-hint ${strengthClass[pwStrength]}`} style={{ marginTop: 4, display: "block" }}>
              비밀번호 강도: {strengthLabel[pwStrength]}
            </span>
          </div>
        )}

        {/* 비밀번호 확인 */}
        <InputField
          label="비밀번호 확인"
          icon={lockIcon}
          hint={pwConfirmHint}
          hintType={touched.passwordConfirm ? (pwMatch ? "success" : "error") : ""}
        >
          <input
            className="input-field"
            type={showPwC ? "text" : "password"}
            placeholder="비밀번호 재입력"
            value={form.passwordConfirm}
            onChange={set("passwordConfirm")}
            onBlur={touch("passwordConfirm")}
            onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
          />
          <button className="password-toggle" onClick={() => setShowPwC(!showPwC)} type="button" tabIndex={-1}>
            <EyeIcon open={showPwC} />
          </button>
        </InputField>

        {/* 서버 에러 */}
        {serverError && (
          <div className="login-error">{serverError}</div>
        )}

        {/* 가입 버튼 */}
        <button
          className="signup-btn"
          onClick={handleSubmit}
          disabled={!canSubmit || loading}
        >
          {loading ? "가입 중..." : "회원가입"}
        </button>

        <p className="login-terms">
          가입 시{" "}
          <span className="login-link">서비스 이용약관</span> 및{" "}
          <span className="login-link">개인정보 처리방침</span>에 동의하게 됩니다.
        </p>
      </div>
    </div>
  );
}