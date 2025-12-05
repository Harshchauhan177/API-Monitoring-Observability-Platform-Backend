"use client";
import { useEffect, useState } from "react";

export default function IssuesPage() {
  const [issues, setIssues] = useState([]);

  function load() {
    fetch("http://localhost:8080/api/issues/unresolved")
      .then((res) => res.json())
      .then((data) => setIssues(data));
  }

  useEffect(() => {
    load();
  }, []);

  function resolve(id: string) {
    fetch(`http://localhost:8080/api/issues/${id}/resolve`, {
      method: "POST",
    }).then(() => load());
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-4">Unresolved Issues</h1>

      {issues.map((i: any) => (
        <div key={i.id} className="border p-3 mb-3 rounded">
          <p>
            <b>{i.issueType}</b> — {i.endpoint}
          </p>

          <button
            onClick={() => resolve(i.id)}
            className="mt-2 bg-green-600 text-white px-3 py-1 rounded"
          >
            Resolve
          </button>
        </div>
      ))}
    </div>
  );
}
