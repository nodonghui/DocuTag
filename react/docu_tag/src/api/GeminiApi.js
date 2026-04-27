const BASE_URL = process.env.REACT_APP_API_URL;

export async function createDocument({content, mode}) {
  const response = await fetch(`${BASE_URL}/api/gemini/summarize`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ content, mode }),
  });

  if (!response.ok) {
    throw new Error(`문서 생성 실패: ${response.status}`);
  }

  const text = await response.text(); // 백엔드가 String 반환하므로
  return text;
}
