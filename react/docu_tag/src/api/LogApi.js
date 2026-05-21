const BASE_URL = process.env.REACT_APP_API_URL;

// 클릭 로그 전송
export async function sendClickLog({ userId, page, meta }) {
  
  console.error("로그 전송");
  const response = await fetch(`${BASE_URL}/api/log/click`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      type: "click",
      userId,
      page,
      meta,
    }),
  });

  if (!response.ok) {
    console.error("클릭 로그 전송 실패");
  }
}

export async function getTodayClickCount() {
    const response = await fetch(`${BASE_URL}/api/stats/clicks/today`, {
        method: "GET",
        credentials: "include",
    });

    if (!response.ok) {
        throw new Error("클릭 수 조회 실패");
    }

    const data = await response.json();
    return data.count; // { count: 123 } 에서 숫자만 반환
}

