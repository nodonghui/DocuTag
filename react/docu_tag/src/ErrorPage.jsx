// ErrorPage.jsx
import { useNavigate } from "react-router-dom";
import "./styles/login.css";

const ERROR_MESSAGE = {
    "-1":    "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    "-2":    "잘못된 요청입니다.",
    "-401":  "인증 정보가 유효하지 않습니다. 다시 로그인해주세요.",
    "-7":    "카카오 서비스 점검 중입니다. 잠시 후 다시 시도해주세요.",
    "-9798": "카카오 서비스 점검 중입니다.",
    "UNKNOWN": "알 수 없는 오류가 발생했습니다.",
};

export default function ErrorPage() {
    const navigate = useNavigate();

    const params = new URLSearchParams(window.location.search);
    const errorCode = params.get("error") ?? "UNKNOWN";
    const errorMsg = ERROR_MESSAGE[errorCode] ?? `오류가 발생했습니다. (${errorCode})`;

    return (
        <div style={{ background: "#0f0f11", minHeight: "100vh" }}>
            {/* 네비 */}
            <div className="error-nav">
                <span
                    className="logo-text"
                    style={{ cursor: "pointer" }}
                    onClick={() => navigate("/")}
                >
                    DocuTag
                </span>
            </div>

            {/* 에러 카드 */}
            <div className="error-container">
                <div className="error-card">
                    <h1 className="error-title">오류가 발생했습니다</h1>
                    <p className="error-message">{errorMsg}</p>
                    <p className="error-code">code: {errorCode}</p>
                    <button
                        className="btn-confirm"
                        style={{ width: "100%", marginTop: "8px" }}
                        onClick={() => navigate("/")}
                    >
                        홈으로 돌아가기
                    </button>
                </div>
            </div>
        </div>
    );
}