import { fetchAPI } from "@/lib/api";

interface ApiIssue {
  id: string;
  serviceName: string;
  endpoint: string;
  description: string;
  resolved: boolean;
  timestamp: string;
}

export default async function IssuesPage() {
  const issues = await fetchAPI<ApiIssue[]>("/api/issues/unresolved");

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4">Unresolved API Issues</h1>

      {issues.length === 0 && (
        <p className="text-gray-600">No unresolved issues 🎉</p>
      )}

      <div className="grid grid-cols-1 gap-4">
        {issues.map((issue) => (
          <div key={issue.id} className="p-4 border rounded-lg bg-white shadow">
            <h2 className="font-bold text-lg text-orange-600">
              ⚠️ Issue in {issue.serviceName}
            </h2>

            <p className="text-gray-700">Endpoint: {issue.endpoint}</p>
            <p className="font-semibold text-red-700">{issue.description}</p>

            <p className="text-gray-500 text-sm mt-2">
              {new Date(issue.timestamp).toLocaleString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
