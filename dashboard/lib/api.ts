export async function fetchAPI<T>(path: string): Promise<T> {
  const baseUrl = "http://localhost:8080";
  const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(baseUrl + path, {
    method: "GET",
    headers,
    cache: "no-store",
  });

  if (!res.ok) {
    if (res.status === 401) {
      // Token expired or invalid, redirect to login
      if (typeof window !== "undefined") {
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
    }
    throw new Error(`Failed to fetch ${path}`);
  }

  return res.json();
}
