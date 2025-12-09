"use client";

export default function LogoutButton() {
  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <a
      href="/login"
      onClick={(e) => {
        e.preventDefault();
        handleLogout();
      }}
      className="hover:text-blue-400 mt-auto"
    >
      Logout
    </a>
  );
}

