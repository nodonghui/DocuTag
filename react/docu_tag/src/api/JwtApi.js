// 토큰 재발급

const BASE_URL = process.env.REACT_APP_API_URL;

// 로그인 상태 확인
export async function checkAuthStatus() {
  const response = await fetch(`${BASE_URL}/api/jwt/me`, {
    credentials: "include",
  });

  console.log("api/jwt/me send");

  if (!response.ok) return null;

  return response.json(); // { nickname: "홍길동" }
}

export async function refreshAccessToken() {
  const response = await fetch(`${BASE_URL}/api/jwt/refresh`, {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok) {
    window.location.href = "/";
    throw new Error("세션이 만료되었습니다. 다시 로그인해주세요.");
  }
}

export async function fetchLogout({ onLogout }) {
    const response = await fetch(`${BASE_URL}/api/jwt/logout`, {
        method: "POST",
        credentials: "include",
    });

    if (!response.ok) {
        window.location.href = "/";
        throw new Error("세션이 만료되었습니다. 다시 로그인해주세요.");
    }

    onLogout();
}

// 공통 fetch 래퍼 (401 시 자동 재시도)
export async function fetchWithAuth(url, options = {}) {
  const config = {
    ...options,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  };

  let response = await fetch(url, config);

  if (response.status === 401) {
    await refreshAccessToken();
    response = await fetch(url, config);
  }

  return response;
}




// 일반 로그인, 회원가입

export async function signup({ email, password, nickname }) {
  const response = await fetch(`${BASE_URL}/api/user/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password, nickname }),
  });

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "회원가입에 실패했습니다.");
  }

  return response;
}

export async function login({ email, password }) {
  const response = await fetch(`${BASE_URL}/api/user/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",  // 쿠키(JWT) 저장을 위해 필요
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "이메일 또는 비밀번호가 올바르지 않습니다.");
  }

  return response;
}