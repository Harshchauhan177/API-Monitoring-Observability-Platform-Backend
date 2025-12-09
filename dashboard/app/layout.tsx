import "./globals.css";
import LogoutButton from "./components/LogoutButton";

export const metadata = {
  title: "API Monitoring Dashboard",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="flex">
        <aside className="w-64 h-screen bg-gray-900 text-white p-6 flex flex-col gap-4">
          <h1 className="text-xl font-bold mb-4">Dashboard</h1>

          <a href="/logs" className="hover:text-blue-400">
            Logs
          </a>
          <a href="/alerts" className="hover:text-blue-400">
            Alerts
          </a>
          <a href="/issues" className="hover:text-blue-400">
            Issues
          </a>
          <LogoutButton />
        </aside>

        <main className="flex-1 p-6">{children}</main>
      </body>
    </html>
  );
}
