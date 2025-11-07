// auth.js
import { db, auth } from "./firebase.js";
import {
  ref,
  get,
  update,
} from "https://www.gstatic.com/firebasejs/11.0.1/firebase-database.js";
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  sendPasswordResetEmail,
  onAuthStateChanged,
  signOut,
} from "https://www.gstatic.com/firebasejs/11.0.1/firebase-auth.js";

/* ---------- shared helpers ---------- */
const emailKey = (e) => e.trim().toLowerCase().replace(/\./g, ",");
const isValidEmail = (e) => !!e && e.includes("@") && e.includes(".");
const exists = async (path) => (await get(ref(db, path))).exists();
const emailExists = (email) => exists(`emails/${emailKey(email)}`);
const usernameExists = (u) => exists(`usernames/${u.trim().toLowerCase()}`);

// Check if user is an operator
const checkOperatorRole = async (uid) => {
  const userRoleRef = ref(db, `users/${uid}/role`);
  const snapshot = await get(userRoleRef);
  return snapshot.exists() && snapshot.val() === "operator";
};

// Handle login navigation based on role
const handleLoginNavigation = async (user) => {
  if (!user) return;
  const isOperator = await checkOperatorRole(user.uid);
  if (isOperator) {
    window.location.href = "OperatorDashboard.html";
  } else {
    window.location.href = "HomePage.html";
  }
};

const registerForm = document.getElementById("registerForm");
if (registerForm) {
  const registerMsg = document.getElementById("registerMsg");
  const emailInput = registerForm.email;
  const usernameInput = registerForm.username;
  const emailStatus = document.getElementById("emailStatus");
  const usernameStatus = document.getElementById("usernameStatus");

  emailInput?.addEventListener("blur", async () => {
    const email = emailInput.value.trim();
    if (!isValidEmail(email)) {
      //if its not a valid email
      emailStatus.textContent = "Invalid email format";
      emailStatus.className = "hint error";
      return;
    }
    emailStatus.textContent = "Checking…"; //else check if it has been used or not
    const taken = await emailExists(email);
    emailStatus.textContent = taken
      ? "Email is already taken"
      : "Email is available";
    emailStatus.className = taken ? "hint error" : "hint ok";
  });

  usernameInput?.addEventListener("blur", async () => {
    const u = usernameInput.value.trim().toLowerCase();
    if (u.length < 3) {
      usernameStatus.textContent = "Username too short";
      usernameStatus.className = "hint error";
      return;
    }
    usernameStatus.textContent = "Checking…";
    const taken = await usernameExists(u);
    usernameStatus.textContent = taken
      ? "Username is already taken"
      : "Username is available";
    usernameStatus.className = taken ? "error" : "ok";
  });

  registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const fullName = registerForm.fullName.value.trim();
    const role = registerForm.role ? registerForm.role.value : "rider";
    const email = registerForm.email.value.trim();
    const paymentInfo = registerForm.paymentInfo.value.trim();
    const address = registerForm.address.value.trim();
    const username = registerForm.username.value.trim().toLowerCase();
    const password = registerForm.password.value.trim();

    if (
      !fullName ||
      !paymentInfo ||
      !address ||
      !isValidEmail(email) ||
      username.length < 3
    ) {
      registerMsg.className = "hint error";
      registerMsg.textContent = "Please fill all fields correctly.";
      return;
    }

    const [emailTaken, userTaken] = await Promise.all([
      emailExists(email),
      usernameExists(username),
    ]);
    if (emailTaken || userTaken) {
      registerMsg.className = "hint error";
      registerMsg.textContent = "Email or username already taken.";
      return;
    }

    try {
      const cred = await createUserWithEmailAndPassword(auth, email, password);
      const uid = cred.user.uid;

      const userData = {
        uid,
        fullName,
        email,
        username,
        paymentInfo,
        address,
        role,
        createdAt: Date.now(),
      };
      await update(ref(db), {
        [`users/${uid}`]: userData,
        [`usernames/${username}`]: uid,
        [`emails/${emailKey(email)}`]: uid,
      });

      registerMsg.className = "ok";
      registerMsg.innerHTML = "Registration successful! Redirecting to login…";
      setTimeout(() => {
        window.location.href = "LoginPage.html";
      }, 1500);
    } catch (err) {
      console.error(err);
      const msg =
        err.code === "auth/email-already-in-use"
          ? "Email already in use."
          : err.code === "auth/invalid-email"
          ? "Invalid email."
          : err.code === "auth/weak-password"
          ? "Password is too weak."
          : err.message || "Registration failed.";
      registerMsg.className = "error";
      registerMsg.textContent = `${msg}`;
    }
  });
}

/* ---------- login ---------- */
const loginForm = document.getElementById("loginForm");
if (loginForm) {
  const loginMsg = document.getElementById("loginMsg");
  const emailEl = document.getElementById("loginEmail");
  const passEl = document.getElementById("loginPassword");
  const forgotBtn = document.getElementById("forgotPassword");

  const POST_LOGIN_REDIRECT = "index.html";

  //   auto-redirect if already logged in
  onAuthStateChanged(auth, (user) => {
    const page = location.pathname.split("/").pop()?.toLowerCase();
    const allowList = ["loginpage.html", "register.html", "forgot.html"]; // add your auth pages
    if (user && !allowList.includes(page)) {
      location.replace("index.html");
    }
  });

  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = emailEl.value.trim();
    const pass = passEl.value.trim();

    if (!isValidEmail(email) || !pass) {
      loginMsg.className = "hint error";
      loginMsg.textContent = "Enter a valid email and password.";
      return;
    }

    try {
      await signInWithEmailAndPassword(auth, email, pass);
      loginMsg.className = "hint ok";
      loginMsg.textContent = "Logged in! Redirecting…";
      setTimeout(() => {
        window.location.href = POST_LOGIN_REDIRECT;
      }, 800);
    } catch (err) {
      console.error(err);
      const msg =
        err.code === "auth/invalid-email"
          ? "Invalid email address."
          : err.code === "auth/user-disabled"
          ? "This account is disabled."
          : err.code === "auth/user-not-found"
          ? "No account with that email."
          : err.code === "auth/wrong-password" ||
            err.code === "auth/invalid-credential"
          ? "Incorrect email or password."
          : err.message || "Login failed.";
      loginMsg.className = "hint error";
      loginMsg.textContent = msg;
    }
  });

  // Logout button
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      console.log("Logout button clicked");
      try {
        await signOut(auth);
        // Redirect back to login page
        window.location.href = "HomePage.html";
      } catch (err) {
        console.error("Logout failed", err);
        alert("Could not log out, please try again.");
      }
    });
  }
}

export async function getIdToken() {
  const u = auth.currentUser;
  if (!u) throw new Error("Not logged in");
  return await u.getIdToken(true);
}

export async function authFetch(url, options = {}) {
  const token = await getIdToken();
  const headers = {
    ...(options.headers || {}),
    Authorization: `Bearer ${token}`,
  };
  return fetch(url, { ...options, headers });
}

export async function getCurrentUsername() {
  const u = auth.currentUser;
  if (!u) throw new Error("Not logged in");

  //username look up
  const snap = await get(ref(db, `users/${u.uid}/username`));
  if (snap.exists()) {
    return snap.val(); //the username from signup
  }
  //username missing
  return u.email || u.uid;
}
