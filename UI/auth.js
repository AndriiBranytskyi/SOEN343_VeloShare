// auth.js
import { db, auth } from "./firebase.js";
import { ref, get, update } from "https://www.gstatic.com/firebasejs/11.0.1/firebase-database.js";
import { createUserWithEmailAndPassword } from "https://www.gstatic.com/firebasejs/11.0.1/firebase-auth.js";

const registerForm   = document.getElementById("registerForm");
const registerMsg    = document.getElementById("registerMsg");
const emailInput     = registerForm.email;
const usernameInput  = registerForm.username;
const passwordInput  = registerForm.password;
const emailStatus    = document.getElementById("emailStatus");
const usernameStatus = document.getElementById("usernameStatus");

const isValidEmail = (e) => !!e && e.includes("@") && e.includes(".");
const emailKey = (e) => e.trim().toLowerCase().replace(/\./g, ",");

async function usernameExists(username) {
  const snap = await get(ref(db, `usernames/${username.trim().toLowerCase()}`));
  return snap.exists();
}
async function emailExists(email) {
  const snap = await get(ref(db, `emails/${emailKey(email)}`));
  return snap.exists();
}

// Live checks (optional)
emailInput.addEventListener("blur", async () => {
  const email = emailInput.value.trim();
  if (!isValidEmail(email)) {
    emailStatus.textContent = "Invalid email format";
    emailStatus.className = "error";
    return;
  }
  emailStatus.textContent = "Checking...";
  emailStatus.className = "";
  emailStatus.textContent = (await emailExists(email)) ? "Email is already taken" : "Email is available";
  emailStatus.className = (await emailExists(email)) ? "error" : "ok";
});

usernameInput.addEventListener("blur", async () => {
  const u = usernameInput.value.trim().toLowerCase();
  if (u.length < 3) {
    usernameStatus.textContent = "Username too short";
    usernameStatus.className = "error";
    return;
  }
  usernameStatus.textContent = "Checking...";
  usernameStatus.className = "";
  usernameStatus.textContent = (await usernameExists(u)) ? "Username is already taken" : "Username is available";
  usernameStatus.className = (await usernameExists(u)) ? "error" : "ok";
});

registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const fullName    = registerForm.fullName.value.trim();
  const email       = emailInput.value.trim();
  const paymentInfo = registerForm.paymentInfo.value.trim();
  const address     = registerForm.address.value.trim();
  const username    = usernameInput.value.trim().toLowerCase();
  const password    = passwordInput.value.trim();

  if (!fullName || !paymentInfo || !address || !isValidEmail(email) || username.length < 3) {
    registerMsg.className = "error";
    registerMsg.textContent = "Please fill all fields correctly.";
    return;
  }

  try {
    // Ensure uniqueness
    const [emailTaken, usernameTaken] = await Promise.all([emailExists(email), usernameExists(username)]);
    if (emailTaken || usernameTaken) {
      registerMsg.className = "error";
      registerMsg.textContent = "Email or username already taken.";
      return;
    }

    // SUPPORT BOTH MODES:
    // - file://  -> RTDB only
    // - http(s)  -> use Auth then write RTDB
    let uid;

    if (location.protocol === "file:") {
      // RTDB-only path (no Auth from file://)
      uid = (crypto?.randomUUID?.() || Math.random().toString(36).slice(2));
    } else {
      // Auth path (requires hosting on http(s) AND allowed domain in Firebase console)
      const cred = await createUserWithEmailAndPassword(auth, email, password);
      uid = cred.user.uid;
    }

    const userData = {
      uid,
      fullName,
      email,
      username,
      paymentInfo,
      address,
      createdAt: Date.now()
    };

    // Atomic multi-path write: user record + indexes
    await update(ref(db), {
      [`users/${uid}`]: userData,
      [`usernames/${username}`]: uid,
      [`emails/${emailKey(email)}`]: uid
    });

    registerMsg.className = "ok";
    registerMsg.textContent = "✅ Registration successful! Redirecting to login page...";
    // redirect after 2 seconds
    setTimeout(() => {
    window.location.href = "LoginPage.html";
  }, 2000);
    
    
    registerForm.reset();
    emailStatus.textContent = "";
    usernameStatus.textContent = "";
  } catch (err) {
    console.error(err);
    registerMsg.className = "error";
    registerMsg.textContent = `❌ Registration failed: ${err.message || err}`;
  }
});
