import { useEffect, useState } from "react";
import { getTodayClickCount } from "./api/LogApi.js";

export default function StatsDashboard() {
    const [count, setCount] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [lastUpdated, setLastUpdated] = useState(null);

    const fetchStats = async () => {
        setLoading(true);
        setError(null);
        try {
            const result = await getTodayClickCount();
            setCount(result);
            setLastUpdated(new Date().toLocaleTimeString());
        } catch (e) {
            setError("데이터 조회 실패");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
    }, []);

    return (
        <div style={{ padding: "2rem" }}>
            <h2>오늘의 클릭 통계</h2>

            <div>
                <p>총 클릭 수: <strong>{loading ? "로딩중..." : count ?? "-"}</strong></p>
                {lastUpdated && <p>마지막 갱신: {lastUpdated}</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
            </div>

            <button onClick={fetchStats} disabled={loading}>
                새로고침
            </button>
        </div>
    );
}