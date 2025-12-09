import { fetchAPI } from "@/lib/api";

interface Alert {
  id: string;
  serviceName: string;
  endpoint: string;
  message: string;
  alertType: string;
  timestamp: string;
}

export default async function AlertsPage() {
  const alerts = await fetchAPI<Alert[]>("/api/alerts");

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-semibold mb-4 text-gray-900">Alerts</h1>

      {alerts.length === 0 && (
        <div className="bg-white p-8 rounded-lg shadow-lg text-center">
          <p className="text-gray-600 text-lg">No alerts yet 😊</p>
        </div>
      )}

      <div className="grid grid-cols-1 gap-4">
        {alerts.map((alert) => (
          <div key={alert.id} className="p-5 border-2 border-red-200 rounded-lg bg-white shadow-lg hover:shadow-xl transition-shadow">
            <h2 className="font-bold text-lg text-red-600 mb-3">
              🚨 {alert.alertType.toUpperCase().replace('_', ' ')}
            </h2>

            <div className="space-y-2">
              <p className="text-gray-800 font-medium">
                <span className="text-gray-600">Service:</span> {alert.serviceName}
              </p>
              <p className="text-gray-800 font-medium">
                <span className="text-gray-600">Endpoint:</span> {alert.endpoint || "(none)"}
              </p>
              <p className="font-semibold text-red-700 text-base mt-3 p-2 bg-red-50 rounded">
                {alert.message}
              </p>

              <p className="text-gray-600 text-sm mt-3 font-medium">
                {new Date(alert.timestamp).toLocaleString()}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
