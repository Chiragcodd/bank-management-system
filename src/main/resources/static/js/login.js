document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  clearErrors();

  const user = {
    username: document.getElementById("username").value.trim(),
    password: document.getElementById("password").value.trim()
  };

  let isValid = true;

  if (!user.username) {
    showFieldError("username", "usernameErr", "Username is required");
    isValid = false;
  } else if (user.username.length < 3) {
    showFieldError("username", "usernameErr", "Username must be at least 3 characters");
    isValid = false;
  }

  if (!user.password) {
    showFieldError("password", "passwordErr", "Password is required");
    isValid = false;
  } else if (user.password.length < 6) {
    showFieldError("password", "passwordErr", "Password must be at least 6 characters");
    isValid = false;
  }

  if (!isValid) return;

  const btn = document.getElementById("loginBtn");
  btn.textContent = "Please wait...";
  btn.classList.add("btn-loading");

  try {
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify(user)
    });

    const resData = await response.json();

    if (!resData.success) {

      showBanner("error", "⚠️  " + resData.message);
      return;
    }

    const loggedUser = resData.data;

    if (!loggedUser.verified) {
      localStorage.setItem("mobileNumber",     loggedUser.mobileNumber);
      localStorage.setItem("usernameAfterOtp", loggedUser.username);
      window.location.href = "otp.html";
      return;
    }

    localStorage.setItem("user",  JSON.stringify(loggedUser));
    localStorage.setItem("token", loggedUser.token);

    showBanner("success", "✅  Login successful! Redirecting...");

    setTimeout(() => {
      if (loggedUser.role === "ADMIN") {
        window.location.href = "admin-dashboard.html";
      } else {
        window.location.href = "dashboard.html";
      }
    }, 800);

  } catch (error) {
    console.error(error);

    showBanner("error", "⚠️  Server error. Make sure backend is running.");
  } finally {
    btn.textContent = "Login";
    btn.classList.remove("btn-loading");
  }
});

function showFieldError(fieldId, errId, message) {
  document.getElementById(fieldId).classList.add("is-invalid");
  const err = document.getElementById(errId);
  err.textContent = message;
  err.classList.add("show");
}

function clearErrors() {
  ["username", "password"].forEach(id => {
    document.getElementById(id).classList.remove("is-invalid");
  });
  ["usernameErr", "passwordErr"].forEach(id => {
    const el = document.getElementById(id);
    el.classList.remove("show");
    el.textContent = "";
  });
  const banner = document.getElementById("alertBanner");
  banner.className   = "alert-banner";
  banner.textContent = "";
}

function showBanner(type, message) {
  const banner = document.getElementById("alertBanner");
  banner.className   = "alert-banner " + type;
  banner.textContent = message;
}

["username", "password"].forEach(id => {
  const errMap = { username: "usernameErr", password: "passwordErr" };
  document.getElementById(id).addEventListener("input", () => {
    document.getElementById(id).classList.remove("is-invalid");
    const errEl = document.getElementById(errMap[id]);
    errEl.classList.remove("show");
    errEl.textContent = "";
  });
});
