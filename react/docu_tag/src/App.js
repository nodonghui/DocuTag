import './styles/App.css';
import { useState, useEffect } from "react";
import LoginPage from "./LoginPage";
import DocuTag from "./DocuTag";

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    // 1. 이미 토큰 있으면 바로 홈으로
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
      return;
    }

    // 2. 백엔드가 토큰을 쿼리파라미터로 넘겨준 경우
    const params = new URLSearchParams(window.location.search);
    const newToken = params.get("token");

    if (newToken) {
      localStorage.setItem("token", newToken);
      setIsLoggedIn(true);
      window.history.replaceState({}, "", "/"); // URL 정리
    }
  }, []);

  return isLoggedIn ? <DocuTag /> : <LoginPage />;
}