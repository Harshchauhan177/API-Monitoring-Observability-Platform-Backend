"use client";

import { useEffect, useState } from "react";
import { fetchAPI } from "@/lib/api";

interface DashboardStats {
  slowApiCount: number;
  brokenApiCount: number;
  rateLimitViolations: number;
  avgLatencyPerEndpoint: Record<string, number>;
  top5SlowEndpoints: Array<{
    endpoint: string;
    serviceName: string;
    avgLatency: number;
    requestCount: number;
  }>;
  errorRate: number;
}

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadStats() {
      try {
        const data = await fetchAPI<DashboardStats>("/api/dashboard/stats");
        setStats(data);
      } catch (err) {
        console.error("Failed to load dashboard stats:", err);
      } finally {
        setLoading(false);
      }
    }
    loadStats();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-gray-600">Loading dashboard...</p>
      </div>
    );
  }

  if (!stats) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p className="text-red-600">Failed to load dashboard stats</p>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">Dashboard Overview</h1>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div className="bg-white p-6 rounded-lg shadow border-l-4 border-yellow-500">
          <h3 className="text-sm font-semibold text-gray-600 mb-2">
            Slow APIs
          </h3>
          <p className="text-3xl font-bold text-yellow-600">
            {stats.slowApiCount}
          </p>
          <p className="text-xs text-gray-500 mt-1">APIs &gt; 500ms</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow border-l-4 border-red-500">
          <h3 className="text-sm font-semibold text-gray-600 mb-2">
            Broken APIs
          </h3>
          <p className="text-3xl font-bold text-red-600">
            {stats.brokenApiCount}
          </p>
          <p className="text-xs text-gray-500 mt-1">5xx Errors</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow border-l-4 border-purple-500">
          <h3 className="text-sm font-semibold text-gray-600 mb-2">
            Rate Limit Violations
          </h3>
          <p className="text-3xl font-bold text-purple-600">
            {stats.rateLimitViolations}
          </p>
          <p className="text-xs text-gray-500 mt-1">Total violations</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow border-l-4 border-blue-500">
          <h3 className="text-sm font-semibold text-gray-600 mb-2">
            Error Rate
          </h3>
          <p className="text-3xl font-bold text-blue-600">
            {stats.errorRate.toFixed(2)}%
          </p>
          <p className="text-xs text-gray-500 mt-1">5xx / Total requests</p>
        </div>
      </div>

      {/* Top 5 Slow Endpoints */}
      <div className="bg-white p-6 rounded-lg shadow mb-6">
        <h2 className="text-xl font-bold mb-4">Top 5 Slowest Endpoints</h2>
        {stats.top5SlowEndpoints.length === 0 ? (
          <p className="text-gray-600">No slow endpoints found</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left p-2">Service</th>
                  <th className="text-left p-2">Endpoint</th>
                  <th className="text-right p-2">Avg Latency (ms)</th>
                  <th className="text-right p-2">Request Count</th>
                </tr>
              </thead>
              <tbody>
                {stats.top5SlowEndpoints.map((endpoint, idx) => (
                  <tr key={idx} className="border-b hover:bg-gray-50">
                    <td className="p-2">{endpoint.serviceName}</td>
                    <td className="p-2 font-mono text-sm">{endpoint.endpoint}</td>
                    <td className="p-2 text-right font-semibold text-red-600">
                      {endpoint.avgLatency.toFixed(2)}
                    </td>
                    <td className="p-2 text-right text-gray-600">
                      {endpoint.requestCount}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Average Latency Per Endpoint */}
      <div className="bg-white p-6 rounded-lg shadow">
        <h2 className="text-xl font-bold mb-4">
          Average Latency Per Endpoint
        </h2>
        {Object.keys(stats.avgLatencyPerEndpoint).length === 0 ? (
          <p className="text-gray-600">No latency data available</p>
        ) : (
          <div className="space-y-2 max-h-96 overflow-y-auto">
            {Object.entries(stats.avgLatencyPerEndpoint)
              .sort((a, b) => b[1] - a[1])
              .map(([endpoint, latency], idx) => (
                <div
                  key={idx}
                  className="flex justify-between items-center p-3 border rounded hover:bg-gray-50"
                >
                  <span className="font-mono text-sm">{endpoint}</span>
                  <span className="font-semibold text-blue-600">
                    {latency.toFixed(2)} ms
                  </span>
                </div>
              ))}
          </div>
        )}
      </div>
    </div>
  );
}

