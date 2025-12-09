"use client";

import { useState, useEffect } from "react";
import { fetchAPI } from "@/lib/api";

interface ApiIssue {
  id: string;
  serviceName: string;
  endpoint: string;
  errorMessage: string;
  issueType: string;
  resolved: boolean;
  version?: number;  // For optimistic locking
  timestamp?: number;
}

interface ResolveResponse {
  success: boolean;
  message: string;
  conflict?: boolean;
}

export default function IssuesPage() {
  const [issues, setIssues] = useState<ApiIssue[]>([]);
  const [loading, setLoading] = useState(true);
  const [resolving, setResolving] = useState<string | null>(null);

  async function loadIssues() {
    setLoading(true);
    try {
      const data = await fetchAPI<ApiIssue[]>("/api/issues/unresolved");
      setIssues(data);
    } catch (err) {
      console.error("Failed to load issues:", err);
      setIssues([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadIssues();
  }, []);

  async function resolveIssue(issueId: string, version?: number) {
    setResolving(issueId);
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/api/issues/${issueId}/resolve`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ version }),
      });

      if (response.ok) {
        const result: ResolveResponse = await response.json();
        if (result.success) {
          // Reload issues after resolving
          await loadIssues();
        } else if (result.conflict) {
          alert(result.message || "Issue was modified by another user. Refreshing...");
          // Reload to get latest version
          await loadIssues();
        } else {
          alert(result.message || "Failed to resolve issue.");
        }
      } else {
        const error = await response.json().catch(() => ({ message: "Unknown error" }));
        alert(error.message || "Failed to resolve issue. Please try again.");
      }
    } catch (err) {
      console.error("Failed to resolve issue:", err);
      alert("Failed to resolve issue. Please try again.");
    } finally {
      setResolving(null);
    }
  }

  if (loading) {
    return (
      <div className="p-6">
        <p className="text-gray-600">Loading issues...</p>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4">Unresolved API Issues</h1>

      {issues.length === 0 && (
        <p className="text-gray-600">No unresolved issues 🎉</p>
      )}

      <div className="grid grid-cols-1 gap-4">
        {issues.map((issue) => (
          <div key={issue.id} className="p-4 border rounded-lg bg-white shadow">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h2 className="font-bold text-lg text-orange-600">
                  ⚠️ Issue in {issue.serviceName}
                </h2>

                <p className="text-gray-700 mt-2">
                  <strong>Endpoint:</strong> {issue.endpoint || "N/A"}
                </p>
                <p className="text-gray-700">
                  <strong>Type:</strong> {issue.issueType}
                </p>
                <p className="font-semibold text-red-700 mt-2">
                  {issue.errorMessage}
                </p>

                {issue.timestamp && (
                  <p className="text-gray-500 text-sm mt-2">
                    {new Date(issue.timestamp).toLocaleString()}
                  </p>
                )}
              </div>
              <button
                onClick={() => resolveIssue(issue.id!, issue.version)}
                disabled={resolving === issue.id}
                className="ml-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
              >
                {resolving === issue.id ? "Resolving..." : "Mark as Resolved"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
