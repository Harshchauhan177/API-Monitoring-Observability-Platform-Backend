"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    // Check if user is authenticated
    const token = localStorage.getItem("token");
    
    if (token) {
      // User is logged in, redirect to dashboard page
      router.push("/dashboard");
    } else {
      // User is not logged in, redirect to login page
      router.push("/login");
    }
  }, [router]);

  // Show loading state while redirecting
  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <div className="text-center">
        <p className="text-gray-600">Redirecting...</p>
      </div>
    </div>
  );
}
