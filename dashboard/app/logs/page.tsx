"use client";

import { useState, useEffect } from "react";
import { fetchAPI } from "@/lib/api";

interface LogEntry {
  id: string;
  serviceName: string;
  endpoint: string;
  method: string;
  statusCode: number;
  latencyMs: number;
  timestamp: number;
}

export default function LogsPage() {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    serviceName: "",
    endpoint: "",
    startDate: "",
    endDate: "",
    statusCode: "",
    slowOnly: false,
    brokenOnly: false,
    rateLimitOnly: false,
  });

  async function loadLogs() {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.serviceName) params.append("serviceName", filters.serviceName);
      if (filters.endpoint) params.append("endpoint", filters.endpoint);
      if (filters.startDate) {
        params.append("startDate", new Date(filters.startDate).getTime().toString());
      }
      if (filters.endDate) {
        params.append("endDate", new Date(filters.endDate).getTime().toString());
      }
      if (filters.statusCode) params.append("statusCode", filters.statusCode);
      if (filters.slowOnly) params.append("slowOnly", "true");
      if (filters.brokenOnly) params.append("brokenOnly", "true");
      if (filters.rateLimitOnly) params.append("rateLimitOnly", "true");

      const url = `/api/logs/filtered${params.toString() ? `?${params.toString()}` : ""}`;
      const data = await fetchAPI<LogEntry[]>(url);
      setLogs(data);
    } catch (err) {
      console.error("Failed to load logs:", err);
      setLogs([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadLogs();
  }, []);

  const handleFilterChange = (key: string, value: any) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleApplyFilters = () => {
    loadLogs();
  };

  const handleResetFilters = () => {
    setFilters({
      serviceName: "",
      endpoint: "",
      startDate: "",
      endDate: "",
      statusCode: "",
      slowOnly: false,
      brokenOnly: false,
      rateLimitOnly: false,
    });
    setTimeout(() => loadLogs(), 100);
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-bold mb-4 text-gray-900">API Logs</h1>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow-lg mb-6">
        <h2 className="text-lg font-semibold mb-4 text-gray-800">Filters</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-semibold mb-1 text-gray-700">Service Name</label>
            <input
              type="text"
              className="w-full border-2 border-gray-300 p-2 rounded focus:border-blue-500 focus:outline-none text-gray-800"
              placeholder="e.g., orders-service"
              value={filters.serviceName}
              onChange={(e) => handleFilterChange("serviceName", e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-semibold mb-1 text-gray-700">Endpoint</label>
            <input
              type="text"
              className="w-full border-2 border-gray-300 p-2 rounded focus:border-blue-500 focus:outline-none text-gray-800"
              placeholder="e.g., /api/orders"
              value={filters.endpoint}
              onChange={(e) => handleFilterChange("endpoint", e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-semibold mb-1 text-gray-700">Start Date</label>
            <input
              type="date"
              className="w-full border-2 border-gray-300 p-2 rounded focus:border-blue-500 focus:outline-none text-gray-800"
              value={filters.startDate}
              onChange={(e) => handleFilterChange("startDate", e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-semibold mb-1 text-gray-700">End Date</label>
            <input
              type="date"
              className="w-full border-2 border-gray-300 p-2 rounded focus:border-blue-500 focus:outline-none text-gray-800"
              value={filters.endDate}
              onChange={(e) => handleFilterChange("endDate", e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-semibold mb-1 text-gray-700">Status Code</label>
            <input
              type="number"
              className="w-full border-2 border-gray-300 p-2 rounded focus:border-blue-500 focus:outline-none text-gray-800"
              placeholder="e.g., 200, 404, 500"
              value={filters.statusCode}
              onChange={(e) => handleFilterChange("statusCode", e.target.value)}
            />
          </div>
          <div className="flex items-center space-x-4">
            <label className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                className="mr-2 w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                checked={filters.slowOnly}
                onChange={(e) => handleFilterChange("slowOnly", e.target.checked)}
              />
              <span className="text-sm text-gray-700 font-medium">Slow APIs (&gt;500ms)</span>
            </label>
          </div>
          <div className="flex items-center space-x-4">
            <label className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                className="mr-2 w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                checked={filters.brokenOnly}
                onChange={(e) => handleFilterChange("brokenOnly", e.target.checked)}
              />
              <span className="text-sm text-gray-700 font-medium">Broken APIs (5xx)</span>
            </label>
          </div>
          <div className="flex items-center space-x-4">
            <label className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                className="mr-2 w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                checked={filters.rateLimitOnly}
                onChange={(e) => handleFilterChange("rateLimitOnly", e.target.checked)}
              />
              <span className="text-sm text-gray-700 font-medium">Rate-limit hits</span>
            </label>
          </div>
        </div>
        <div className="mt-4 flex gap-2">
          <button
            onClick={handleApplyFilters}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Apply Filters
          </button>
          <button
            onClick={handleResetFilters}
            className="bg-gray-300 text-gray-700 px-4 py-2 rounded hover:bg-gray-400"
          >
            Reset
          </button>
        </div>
      </div>

      {/* Logs Table */}
      {loading ? (
        <div className="bg-white p-8 rounded-lg shadow-lg text-center">
          <p className="text-gray-700 font-medium text-lg">Loading logs...</p>
        </div>
      ) : logs.length === 0 ? (
        <div className="bg-white p-8 rounded-lg shadow-lg text-center">
          <p className="text-gray-600 text-lg">No logs found.</p>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-lg overflow-x-auto">
          <table className="min-w-full">
            <thead className="bg-gray-100 border-b-2 border-gray-300">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Service</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Endpoint</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Method</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Status</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Latency</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700 uppercase tracking-wider">Timestamp</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3 text-gray-800 font-medium">{log.serviceName}</td>
                  <td className="px-4 py-3 font-mono text-sm text-gray-700">{log.endpoint}</td>
                  <td className="px-4 py-3">
                    <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs font-semibold">
                      {log.method}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-2 py-1 rounded text-xs font-semibold ${
                        log.statusCode >= 500
                          ? "bg-red-100 text-red-800 border border-red-200"
                          : log.statusCode >= 400
                          ? "bg-yellow-100 text-yellow-800 border border-yellow-200"
                          : "bg-green-100 text-green-800 border border-green-200"
                      }`}
                    >
                      {log.statusCode}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`font-semibold ${
                        log.latencyMs > 500
                          ? "text-red-600"
                          : log.latencyMs > 200
                          ? "text-yellow-600"
                          : "text-green-600"
                      }`}
                    >
                      {log.latencyMs} ms
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-700 font-medium">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
