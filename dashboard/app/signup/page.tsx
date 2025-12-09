"use client";

import { useState } from "react";
import Link from "next/link";

export default function SignupPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function signup() {
    try {
      setError(""); // Clear previous errors
      setSuccess(""); // Clear previous success messages

      // Validation
      if (!username || !password || !confirmPassword) {
        setError("All fields are required");
        return;
      }

      if (password !== confirmPassword) {
        setError("Passwords do not match");
        return;
      }

      if (password.length < 3) {
        setError("Password must be at least 3 characters");
        return;
      }

      const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      const data = await res.json();

      if (data.message) {
        setSuccess(data.message);
        // Redirect to login after 1.5 seconds
        setTimeout(() => {
          window.location.href = "/login";
        }, 1500);
      } else {
        setError(data.error || "Failed to register user");
      }
    } catch (err) {
      setError("Failed to connect to server. Please try again.");
    }
  }

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <div className="p-8 bg-white shadow-lg rounded-lg w-96">
        <h1 className="text-2xl font-bold mb-6">Sign Up</h1>

        {error && <p className="text-red-500 mb-3">{error}</p>}
        {success && <p className="text-green-500 mb-3">{success}</p>}

        <input
          className="border p-2 rounded w-full mb-3"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          className="border p-2 rounded w-full mb-3"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="password"
          className="border p-2 rounded w-full mb-3"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <button
          onClick={signup}
          className="bg-blue-600 text-white px-4 py-2 rounded w-full mb-3"
        >
          Sign Up
        </button>

        <p className="text-center text-sm text-gray-600">
          Already have an account?{" "}
          <Link href="/login" className="text-blue-600 hover:underline">
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}

