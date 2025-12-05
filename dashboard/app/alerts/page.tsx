"use client";
import { useEffect, useState } from "react";

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/alerts/all")
      .then((res) => res.json())
      .then((data) => setAlerts(data));
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-4">Alerts</h1>

      {alerts.map((a: any) => (
        <div key={a.id} className="border p-3 mb-3 bg-red-50 rounded">
          <b>{a.alertType.toUpperCase()}</b> — {a.message}
          <br />
          <span className="text-sm">Service: {a.serviceName}</span>
        </div>
      ))}
    </div>
  );
}
