# Login & Signup Functionality Testing Guide

## Prerequisites
- MongoDB should be running (ports 27017 and 27018)
- Java 17 installed
- Node.js and npm installed

---

## Step-by-Step Instructions

### Step 1: Start the Backend Service

Open a terminal and run:

```bash
cd collector-service
./gradlew bootRun
```

Wait until you see: `Started CollectorServiceApplication` in the console.
The backend will be running on `http://localhost:8080`

**Keep this terminal window open!**

---

### Step 2: Start the Frontend Dashboard

Open a **NEW terminal window** (keep backend running) and run:

```bash
cd dashboard
npm install  # Only needed first time
npm run dev
```

Wait until you see: `Ready on http://localhost:3000`

**Keep this terminal window open too!**

---

## Signup Functionality

### Step 3: Test Signup in Browser

1. Open your web browser
2. Go to: `http://localhost:3000/signup`
   - **OR** click "Sign Up" link from the login page
3. You should see a signup form with:
   - Username field
   - Password field
   - Confirm Password field
   - Sign Up button
   - Link to login page

4. Fill in the form:
   - **Username:** Choose a unique username (e.g., `newuser`)
   - **Password:** Enter a password (minimum 3 characters)
   - **Confirm Password:** Re-enter the same password

5. Click the **"Sign Up"** button

**What should happen:**
- ✅ If successful: You'll see a green success message and be redirected to `/login` page after 1.5 seconds
- ❌ If username exists: You'll see "User already exists" error
- ❌ If passwords don't match: You'll see "Passwords do not match" error
- ❌ If password too short: You'll see "Password must be at least 3 characters" error

---

## Login Functionality

### Step 4: Test Login in Browser

1. Open your web browser
2. Go to: `http://localhost:3000/login`
3. You should see a login form with:
   - Username field
   - Password field
   - Login button

4. Enter the credentials you registered:
   - **Username:** `testuser`
   - **Password:** `testpass`

5. Click the **"Login"** button

**What should happen:**
- ✅ If credentials are correct: You'll be redirected to `/logs` page
- ✅ The JWT token will be saved in browser's localStorage
- ❌ If credentials are wrong: You'll see an error message in red

---

### Step 5: Verify Token Storage (After Login)

After successful login, verify the token is stored:

1. Open browser Developer Tools (F12 or Right-click → Inspect)
2. Go to **Application** tab (Chrome) or **Storage** tab (Firefox)
3. Click on **Local Storage** → `http://localhost:3000`
4. You should see a key called `token` with a long JWT token value

---

## Troubleshooting

### Backend not starting?
- Check if port 8080 is already in use: `lsof -i :8080`
- Check MongoDB is running: `pgrep -fl mongod`
- Check Java version: `java -version` (should be 17+)

### Frontend not starting?
- Make sure you're in the `dashboard` directory
- Run `npm install` first if you haven't
- Check if port 3000 is in use: `lsof -i :3000`

### Login not working?
- Check browser console (F12) for errors
- Verify backend is running: `curl http://localhost:8080/api/auth/register`
- Check CORS errors in browser console
- Verify the URL in login page is `http://localhost:8080/api/auth/login`

### "Failed to connect to server" error?
- Make sure backend is running on port 8080
- Check backend terminal for errors
- Try: `curl http://localhost:8080/api/auth/register` to test backend

### "Invalid username or password" error?
- Make sure you registered the user first (Step 2)
- Check username/password spelling
- Try registering again with the same credentials

---

## Testing Login via curl (Alternative Method)

You can also test login directly via curl:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

**Expected Response:**
```json
{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY..."}
```

If you see an error:
```json
{"error":"Invalid username or password"}
```

---

## Quick Test Commands

**Register a user via API (alternative to web signup):**
```bash
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpass"}'
```

**Expected Response (JSON):**
- Success: `{"message":"User registered successfully"}`
- Error: `{"error":"User already exists"}` or `{"error":"username missing"}`

**Login via API:**
```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpass"}'
```

**Expected Response (JSON):**
- Success: `{"token":"eyJhbGciOiJIUzI1NiJ9..."}`
- Error: `{"error":"Invalid username or password"}`

**Check if backend is running:**
```bash
curl http://localhost:8080/api/auth/register
```

---

## Next Steps

After successful login:
- The token is automatically included in API requests via `lib/api.ts`
- Protected routes can check for authentication
- You can access `/logs`, `/issues`, `/alerts` pages (if they require auth)

