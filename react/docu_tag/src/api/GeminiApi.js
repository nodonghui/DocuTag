import { fetchWithAuth } from "./JwtApi";

const BASE_URL = process.env.REACT_APP_API_URL;

export async function createDocument({ content, mode }) {
  const response = await fetchWithAuth(`${BASE_URL}/api/gemini/summarize`, {
    method: "POST",
    body: JSON.stringify({ content, mode }),
  });

  if (!response.ok) throw new Error(`문서 생성 실패: ${response.status}`);

  return response.text();
}