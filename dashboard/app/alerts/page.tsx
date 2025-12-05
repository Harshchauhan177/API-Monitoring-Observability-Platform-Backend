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
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-4">Alerts</h1>

      {alerts.length === 0 && <p className="text-gray-600">No alerts yet 😊</p>}

      <div className="grid grid-cols-1 gap-4">
        {alerts.map((alert) => (
          <div key={alert.id} className="p-4 border rounded-lg bg-white shadow">
            <h2 className="font-bold text-lg text-red-600">
              🚨 {alert.alertType.toUpperCase()}
            </h2>

            <p className="text-gray-700">Service: {alert.serviceName}</p>
            <p className="text-gray-700">
              Endpoint: {alert.endpoint || "(none)"}
            </p>
            <p className="font-semibold text-red-700">{alert.message}</p>

            <p className="text-gray-500 text-sm mt-2">
              {new Date(alert.timestamp).toLocaleString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
