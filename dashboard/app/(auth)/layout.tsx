export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // Auth pages don't need sidebar - full screen layout
  // This layout wraps login and signup pages
  return <>{children}</>;
}

