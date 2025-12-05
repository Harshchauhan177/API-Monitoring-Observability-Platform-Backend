"use client";
import { useEffect, useState } from "react";

export default function LogsPage() {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    fetch("http://localhost:8080/api/logs/all", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => setLogs(data));
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-4">API Logs</h1>

      <table className="w-full border">
        <thead>
          <tr className="bg-gray-200">
            <th className="border p-2">Service</th>
            <th className="border p-2">Endpoint</th>
            <th className="border p-2">Method</th>
            <th className="border p-2">Status</th>
            <th className="border p-2">Latency</th>
          </tr>
        </thead>

        <tbody>
          {logs.map((log: any) => (
            <tr key={log.id}>
              <td className="border p-2">{log.serviceName}</td>
              <td className="border p-2">{log.endpoint}</td>
              <td className="border p-2">{log.method}</td>
              <td className="border p-2">{log.statusCode}</td>
              <td className="border p-2">{log.latencyMs} ms</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
