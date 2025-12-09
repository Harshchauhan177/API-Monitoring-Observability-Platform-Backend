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
  errorRateOverTime: Array<{
    timestamp: number;
    errorRate: number;
    totalRequests: number;
    errorRequests: number;
  }>;
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
    <div className="p-6 bg-gray-50 min-h-screen">
      <h1 className="text-3xl font-bold mb-6 text-gray-900">Dashboard Overview</h1>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div className="bg-white p-6 rounded-lg shadow-lg border-l-4 border-yellow-500 hover:shadow-xl transition-shadow">
          <h3 className="text-sm font-semibold text-gray-700 mb-2 uppercase tracking-wide">
            Slow APIs
          </h3>
          <p className="text-3xl font-bold text-yellow-600 mb-1">
            {stats.slowApiCount}
          </p>
          <p className="text-xs text-gray-600 mt-1 font-medium">APIs &gt; 500ms</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-lg border-l-4 border-red-500 hover:shadow-xl transition-shadow">
          <h3 className="text-sm font-semibold text-gray-700 mb-2 uppercase tracking-wide">
            Broken APIs
          </h3>
          <p className="text-3xl font-bold text-red-600 mb-1">
            {stats.brokenApiCount}
          </p>
          <p className="text-xs text-gray-600 mt-1 font-medium">5xx Errors</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-lg border-l-4 border-purple-500 hover:shadow-xl transition-shadow">
          <h3 className="text-sm font-semibold text-gray-700 mb-2 uppercase tracking-wide">
            Rate Limit Violations
          </h3>
          <p className="text-3xl font-bold text-purple-600 mb-1">
            {stats.rateLimitViolations}
          </p>
          <p className="text-xs text-gray-600 mt-1 font-medium">Total violations</p>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-lg border-l-4 border-blue-500 hover:shadow-xl transition-shadow">
          <h3 className="text-sm font-semibold text-gray-700 mb-2 uppercase tracking-wide">
            Error Rate
          </h3>
          <p className="text-3xl font-bold text-blue-600 mb-1">
            {stats.errorRate.toFixed(2)}%
          </p>
          <p className="text-xs text-gray-600 mt-1 font-medium">5xx / Total requests</p>
        </div>
      </div>

      {/* Top 5 Slow Endpoints */}
      <div className="bg-white p-6 rounded-lg shadow mb-6">
        <h2 className="text-xl font-bold mb-4 text-gray-800">Top 5 Slowest Endpoints</h2>
        {stats.top5SlowEndpoints.length === 0 ? (
          <p className="text-gray-600">No slow endpoints found</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b-2 border-gray-300 bg-gray-50">
                  <th className="text-left p-3 text-sm font-semibold text-gray-700">Service</th>
                  <th className="text-left p-3 text-sm font-semibold text-gray-700">Endpoint</th>
                  <th className="text-right p-3 text-sm font-semibold text-gray-700">Avg Latency (ms)</th>
                  <th className="text-right p-3 text-sm font-semibold text-gray-700">Request Count</th>
                </tr>
              </thead>
              <tbody>
                {stats.top5SlowEndpoints.map((endpoint, idx) => (
                  <tr key={idx} className="border-b hover:bg-gray-50">
                    <td className="p-3 text-gray-800 font-medium">{endpoint.serviceName}</td>
                    <td className="p-3 font-mono text-sm text-gray-700">{endpoint.endpoint}</td>
                    <td className="p-3 text-right font-semibold text-red-600">
                      {endpoint.avgLatency.toFixed(2)}
                    </td>
                    <td className="p-3 text-right text-gray-700 font-medium">
                      {endpoint.requestCount}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Error Rate Graph */}
      <div className="bg-white p-6 rounded-lg shadow mb-6">
        <h2 className="text-xl font-bold mb-4 text-gray-800">Error Rate Over Time (Last 24 Hours)</h2>
        {stats.errorRateOverTime.length === 0 ? (
          <p className="text-gray-600">No error rate data available</p>
        ) : (
          <div className="space-y-4">
            <div className="flex items-end justify-between h-64 border-b-2 border-l-2 border-gray-400 pb-2 pl-2">
              {stats.errorRateOverTime.map((point, idx) => {
                const maxErrorRate = Math.max(...stats.errorRateOverTime.map(p => p.errorRate), 1);
                const heightPercent = maxErrorRate > 0 ? (point.errorRate / maxErrorRate) * 100 : 0;
                return (
                  <div key={idx} className="flex flex-col items-center flex-1 mx-1">
                    <div
                      className="w-full bg-red-500 rounded-t hover:bg-red-600 transition-colors relative group cursor-pointer"
                      style={{ height: `${Math.max(heightPercent, 2)}%` }}
                      title={`${point.errorRate.toFixed(2)}% (${point.errorRequests}/${point.totalRequests})`}
                    >
                      <div className="absolute bottom-full mb-1 left-1/2 transform -translate-x-1/2 bg-gray-900 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 whitespace-nowrap z-10 shadow-lg">
                        {point.errorRate.toFixed(2)}%
                        <br />
                        {point.errorRequests}/{point.totalRequests} errors
                      </div>
                    </div>
                    <span className="text-xs text-gray-700 mt-2 font-medium transform -rotate-45 origin-top-left whitespace-nowrap">
                      {new Date(point.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                );
              })}
            </div>
            <div className="flex justify-between text-sm text-gray-700 font-medium">
              <span>0%</span>
              <span className="text-gray-800">Time (Last 24 Hours)</span>
              <span>{Math.max(...stats.errorRateOverTime.map(p => p.errorRate), 0).toFixed(1)}%</span>
            </div>
          </div>
        )}
      </div>

      {/* Average Latency Per Endpoint */}
      <div className="bg-white p-6 rounded-lg shadow">
        <h2 className="text-xl font-bold mb-4 text-gray-800">
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
                  className="flex justify-between items-center p-3 border border-gray-200 rounded hover:bg-gray-50 hover:border-gray-300 transition-colors"
                >
                  <span className="font-mono text-sm text-gray-800">{endpoint}</span>
                  <span className="font-semibold text-blue-600 text-base">
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

