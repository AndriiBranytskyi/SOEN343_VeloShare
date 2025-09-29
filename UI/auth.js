// for register

console.log("auth.js loaded!");

// getting the form from register
const registerForm = document.getElementById('registerForm');
const registerMsg  = document.getElementById('registerMsg');

// validate the input fields of email and username
const emailInput    = registerForm.email;
const usernameInput = registerForm.username;

// email and username status
const emailStatus    = document.getElementById('emailStatus');
const usernameStatus = document.getElementById('usernameStatus');

// helper functions
// email format check
const isValidEmail = (e) => e && e.includes('@') && e.includes('.');

// check if email is available from backend
async function checkEmailAvailability(email) {
  try {
    const res = await fetch(`/api/auth/availability/email?value=${encodeURIComponent(email)}`);
    if (!res.ok) throw new Error('Server error');
    const data = await res.json();
    return data.available; // true if available, false if taken
  } catch (err) {
    console.error('Email check failed', err);
    return false; // assume taken if error
  }
}

// check if username is available from backend
async function checkUsernameAvailability(username) {
  try {
    const res = await fetch(`/api/auth/availability/username?value=${encodeURIComponent(username)}`);
    if (!res.ok) throw new Error('Server error');
    const data = await res.json();
    return data.available;
  } catch (err) {
    console.log('Username check failed', err);
    return false; // assume taken if error
  }
}

// validate email and username while typing (on blur)
emailInput.addEventListener("blur", async () => {
  if (!isValidEmail(emailInput.value)) {
    emailStatus.textContent = 'Invalid email format';
    emailStatus.className = 'error';
    return;
  }
  const available = await checkEmailAvailability(emailInput.value);
  if (available) {
    emailStatus.textContent = 'Email is available';
    emailStatus.className = 'ok';
  } else {
    emailStatus.textContent = 'Email is already taken';
    emailStatus.className = 'error';
  }
});

usernameInput.addEventListener("blur", async () => {
  if (usernameInput.value.trim().length < 3) {
    usernameStatus.textContent = 'Username too short';
    usernameStatus.className = 'error';
    return;
  }
  const available = await checkUsernameAvailability(usernameInput.value);
  if (available) {
    usernameStatus.textContent = 'Username is available';
    usernameStatus.className = 'ok';
  } else {
    usernameStatus.textContent = "Username is already taken";
    usernameStatus.className = 'error';
  }
});

// add a submit handler for when the form is submitted
registerForm.addEventListener('submit', async (event) => {
  event.preventDefault(); // prevent the default form submission behavior

  // run availability checks again before submitting
  const emailAvailable    = await checkEmailAvailability(emailInput.value);
  const usernameAvailable = await checkUsernameAvailability(usernameInput.value);

  if (!emailAvailable || !usernameAvailable) {
    registerMsg.className = 'error';
    registerMsg.textContent = 'Please fix any errors before registering';
    return;
  }

  // if both email and username are available --> success
  registerMsg.className = 'ok';
  registerMsg.textContent = 'Account created successfully, please sign in!';

  console.log('registration ready to be sent to backend');

  // TODO: build payload and POST to /api/auth/register
});
