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
      <div className="p-6 bg-gray-50 min-h-screen">
        <p className="text-gray-700 font-medium">Loading issues...</p>
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-semibold mb-4 text-gray-900">Unresolved API Issues</h1>

      {issues.length === 0 && (
        <div className="bg-white p-8 rounded-lg shadow-lg text-center">
          <p className="text-gray-600 text-lg">No unresolved issues 🎉</p>
        </div>
      )}

      <div className="grid grid-cols-1 gap-4">
        {issues.map((issue) => (
          <div key={issue.id} className="p-5 border-2 border-orange-200 rounded-lg bg-white shadow-lg hover:shadow-xl transition-shadow">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h2 className="font-bold text-lg text-orange-600 mb-3">
                  ⚠️ Issue in {issue.serviceName}
                </h2>

                <div className="space-y-2">
                  <p className="text-gray-800 font-medium">
                    <span className="text-gray-600">Endpoint:</span> {issue.endpoint || "N/A"}
                  </p>
                  <p className="text-gray-800 font-medium">
                    <span className="text-gray-600">Type:</span> <span className="uppercase">{issue.issueType.replace('_', ' ')}</span>
                  </p>
                  <p className="font-semibold text-red-700 mt-3 p-2 bg-red-50 rounded text-base">
                    {issue.errorMessage}
                  </p>

                  {issue.timestamp && (
                    <p className="text-gray-600 text-sm mt-3 font-medium">
                      {new Date(issue.timestamp).toLocaleString()}
                    </p>
                  )}
                </div>
              </div>
              <button
                onClick={() => resolveIssue(issue.id!, issue.version)}
                disabled={resolving === issue.id}
                className="ml-4 bg-green-600 text-white px-5 py-2 rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed font-semibold shadow-md hover:shadow-lg transition-all"
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
