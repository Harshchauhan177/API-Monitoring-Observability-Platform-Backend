export async function fetchAPI<T>(path: string): Promise<T> {
  const baseUrl = "http://localhost:8080";

  const res = await fetch(baseUrl + path, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error(`Failed to fetch ${path}`);
  }

  return res.json();
}
