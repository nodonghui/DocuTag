// App.jsx - 라우팅 추가
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./LoginPage";
import DocuTag from "./DocuTag";
import ErrorPage from "./ErrorPage";
import SignupPage from "./SignupPage";
import { useEffect, useState } from "react";

import { checkAuthStatus } from "./api/JwtApi";

export default function App() {
    const [user, setUser] = useState(undefined); // undefined = 로딩중, null = 비로그인\

   console.log(LoginPage);
   console.log(DocuTag);

    useEffect(() => {
        checkAuthStatus()
            .then(data => setUser(data))   // { nickname: "..." } or null
            .catch(() => setUser(null));
    }, []);

    

    if (user === undefined) return null; // 로딩 중

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
            </Routes>
        </BrowserRouter>
    );
}