document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  clearErrors();

  const user = {
    fullName:     document.getElementById("fullName").value.trim(),
    email:        document.getElementById("email").value.trim(),
    username:     document.getElementById("username").value.trim(),
    password:     document.getElementById("password").value.trim(),
    mobileNumber: document.getElementById("mobileNumber").value.trim(),
    accountType:  document.getElementById("accountType").value
  };

  let isValid = true;

  if (!user.fullName) {
    showFieldError("fullName", "fullNameErr");
    isValid = false;
  }

  if (!/^\S+@\S+\.\S+$/.test(user.email)) {
    showFieldError("email", "emailErr");
    isValid = false;
  }

  if (user.username.length < 3) {
    showFieldError("username", "usernameErr");
    isValid = false;
  }

  if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/.test(user.password)) {
    showFieldError("password", "passwordErr");
    isValid = false;
  }

  if (!/^\d{10}$/.test(user.mobileNumber)) {
    showFieldError("mobileNumber", "mobileErr");
    isValid = false;
  }

  if (!user.accountType) {
    showFieldError("accountType", "accountTypeErr");
    isValid = false;
  }

  if (!isValid) return;

  const btn = document.getElementById("signupBtn");
  btn.textContent = "Please wait...";
  btn.classList.add("btn-loading");

  try {
    const response = await fetch("http://localhost:8080/api/auth/signup", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify(user),
    });

    const resData = await response.json();

    if (!resData.success) {

      showBanner("error", "⚠️  " + resData.message);
      return;
    }

    showBanner("success", "✅  Signup successful! Redirecting to OTP verification...");

    localStorage.setItem("mobileNumber",     user.mobileNumber);
    localStorage.setItem("usernameAfterOtp", user.username);

    setTimeout(() => {
      window.location.href = "otp.html";
    }, 1200);

  } catch (error) {
    console.error(error);

    showBanner("error", "⚠️  Cannot connect to server. Make sure backend is running.");
  } finally {
    btn.textContent = "Sign Up";
    btn.classList.remove("btn-loading");
  }
});

function showFieldError(fieldId, errId) {
  document.getElementById(fieldId).classList.add("is-invalid");
  document.getElementById(errId).classList.add("show");
}

function clearErrors() {

  ["fullName", "email", "username", "password", "mobileNumber", "accountType"]
    .forEach(id => document.getElementById(id).classList.remove("is-invalid"));

  ["fullNameErr", "emailErr", "usernameErr", "passwordErr", "mobileErr", "accountTypeErr"]
    .forEach(id => document.getElementById(id).classList.remove("show"));

  const banner = document.getElementById("alertBanner");
  banner.className = "alert-banner";
  banner.textContent = "";
}

function showBanner(type, message) {
  const banner = document.getElementById("alertBanner");
  banner.className = "alert-banner " + type;
  banner.textContent = message;

  banner.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

["fullName", "email", "username", "password", "mobileNumber", "accountType"]
  .forEach(id => {
    document.getElementById(id).addEventListener("input", () => {
      document.getElementById(id).classList.remove("is-invalid");

      const errMap = {
        fullName:    "fullNameErr",
        email:       "emailErr",
        username:    "usernameErr",
        password:    "passwordErr",
        mobileNumber:"mobileErr",
        accountType: "accountTypeErr"
      };
      if (errMap[id]) {
        document.getElementById(errMap[id]).classList.remove("show");
      }
    });
  });