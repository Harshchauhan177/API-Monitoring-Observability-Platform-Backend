import { fetchAPI } from "@/lib/api";

export default async function LogsPage() {
  // Fetch logs from backend
  const logs = await fetchAPI<any[]>("/api/logs/all");

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">API Logs</h1>

      {logs.length === 0 && <p>No logs found.</p>}

      <div className="space-y-4">
        {logs.map((log, index) => (
          <div key={index} className="p-4 border rounded-lg shadow bg-red">
            <p>
              <strong>Service:</strong> {log.serviceName}
            </p>
            <p>
              <strong>Endpoint:</strong> {log.endpoint}
            </p>
            <p>
              <strong>Method:</strong> {log.method}
            </p>
            <p>
              <strong>Status:</strong> {log.statusCode}
            </p>
            <p>
              <strong>Latency:</strong> {log.latencyMs} ms
            </p>
            <p className="text-gray-600 text-sm">
              {new Date(log.timestamp).toLocaleString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
