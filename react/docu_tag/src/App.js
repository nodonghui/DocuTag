// App.jsx - 라우팅 추가
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./LoginPage";
import DocuTag from "./DocuTag";
import ErrorPage from "./ErrorPage";
import { useEffect, useState } from "react";

export default function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) { setIsLoggedIn(true); return; }

        const params = new URLSearchParams(window.location.search);
        const newToken = params.get("token");
        if (newToken) {
            localStorage.setItem("token", newToken);
            setIsLoggedIn(true);
            window.history.replaceState({}, "", "/");
        }
    }, []);

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/error"  element={<ErrorPage />} />
                <Route path="/"       element={isLoggedIn ? <DocuTag /> : <LoginPage />} />
            </Routes>
        </BrowserRouter>
    );
}