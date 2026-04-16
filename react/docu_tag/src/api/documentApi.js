export async function createDocument({ title, content, tags }) {
  const response = await fetch("http://localhost:8080/api/documents", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ title, content, tags }),
  });

  if (!response.ok) {
    throw new Error(`문서 생성 실패: ${response.status}`);
  }

}

// api/documentApi.js에 추가
export const fetchDocumentById = async (id) => {
  const response = await fetch(`http://localhost:8080/api/documents/${id}`);
  console.log("상태코드 : " + response.status);
  if (!response.ok) throw new Error("문서 조회 실패");
  return response.json();
};


export async function fetchDocuments({ tags, title, lastId, size }) {
  const params = new URLSearchParams();

  if (tags?.length) tags.forEach((tag) => params.append("tags", tag));
  if (title) params.append("title", title);
  if (lastId != null) params.append("lastId", lastId);
  params.append("size", size ?? 10);

  const response = await fetch(`http://localhost:8080/api/documents?${params}`);
  if (!response.ok) throw new Error(`문서 조회 실패: ${response.status}`);

  return response.json();
}

export const updateDocument = async ({ id, title, content, tags }) => {
  const response = await fetch(`http://localhost:8080/api/documents/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, content, tags }),
  });
  if (!response.ok) throw new Error("문서 수정 실패");
};

export const deleteDocument = async (id) => {
  console.log("delete id : " + id)
  const response = await fetch(`http://localhost:8080/api/documents/${id}`, {
    method: "DELETE",
  });
  if (!response.ok) throw new Error("문서 삭제 실패");
};