// App.jsx - 라우팅 추가
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./LoginPage";
import DocuTag from "./DocuTag";
import ErrorPage from "./ErrorPage";
import SignupPage from "./SignupPage";
import StatsDashboard from "./StatsDashboard";
import { useEffect, useState } from "react";

import { checkAuthStatus } from "./api/JwtApi";
import { sendClickLog } from "./api/LogApi";

export default function App() {
    const [user, setUser] = useState(undefined);

    console.log(LoginPage);
    console.log(DocuTag);

    useEffect(() => {
        checkAuthStatus()
            .then(data => setUser(data))
            .catch(() => setUser(null));
    }, []);

    // 클릭 로그 전송
    useEffect(() => {
        if (!user) return; // 비로그인 시 로그 수집 안함

        const handleClick = (e) => {
            console.log(JSON.stringify(user, null, 2));
            sendClickLog({
                userId: user.userName,
                page: window.location.pathname,
                meta: {
                    tagName: e.target.tagName,
                    id: e.target.id,
                    className: e.target.className,
                    x: e.clientX,
                    y: e.clientY,
                },
            });
        };

        document.addEventListener("click", handleClick);

        return () => document.removeEventListener("click", handleClick); // 메모리 누수 방지
    }, [user]); // user 바뀔 때마다 리스너 갱신

    if (user === undefined) return null;

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/error" element={<ErrorPage />} />
                <Route
                    path="/"
                    element={
                        user
                            ? <DocuTag user={user} onLogout={() => setUser(null)} />
                            : <LoginPage />
                    }
                />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/stats" element={<StatsDashboard />} />
            </Routes>
        </BrowserRouter>
    );
}