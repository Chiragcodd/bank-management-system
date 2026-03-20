document.addEventListener("DOMContentLoaded", () => {

  const mobile = localStorage.getItem("mobileNumber");

  if (!mobile) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("mobileNumber").value = mobile;
});

document.getElementById("otpForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  clearErrors();

  const mobile = document.getElementById("mobileNumber").value.trim();
  const otp    = document.getElementById("otp").value.trim();

  if (!otp || otp.length !== 6 || !/^\d+$/.test(otp)) {
    showFieldError("otp", "otpErr", "Enter valid 6-digit OTP");
    return;
  }

  const btn = document.getElementById("verifyBtn");
  btn.textContent = "Verifying...";
  btn.classList.add("btn-loading");

  try {
    const response = await fetch("http://localhost:8080/api/auth/verify-otp", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ mobileNumber: mobile, otp: otp })
    });

    const data = await response.json();

    if (!data.success) {

      showBanner("error", "⚠️  " + (data.message || "Invalid OTP"));
      return;
    }

    showBanner("success", "✅  Mobile verified successfully! Redirecting to login...");
    localStorage.removeItem("mobileNumber");
    localStorage.removeItem("usernameAfterOtp");

    setTimeout(() => {
      window.location.href = "login.html";
    }, 1200);

  } catch (error) {
    console.error(error);

    showBanner("error", "⚠️  Server error! Make sure backend is running.");
  } finally {
    btn.textContent = "Verify OTP";
    btn.classList.remove("btn-loading");
  }
});

let timeLeft   = 300;
const timerEl  = document.getElementById("timer");
const resendBtn = document.getElementById("resendOtpBtn");

function startTimer() {
  timeLeft = 300;
  resendBtn.disabled = true;

  const interval = setInterval(() => {
    const minutes = Math.floor(timeLeft / 60);
    const seconds = timeLeft % 60;
    timerEl.textContent = `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
    timeLeft--;

    if (timeLeft < 0) {
      clearInterval(interval);
      resendBtn.disabled   = false;
      timerEl.textContent  = "Expired";
      timerEl.style.color  = "var(--red)";
    }
  }, 1000);

  return interval;
}

let currentInterval = startTimer();

resendBtn.addEventListener("click", async () => {
  const mobileNumber = localStorage.getItem("mobileNumber");

  if (!mobileNumber) {

    showBanner("error", "⚠️  Mobile number not found. Please signup again.");
    return;
  }

  resendBtn.textContent = "Sending...";
  resendBtn.disabled    = true;

  try {
    const response = await fetch("http://localhost:8080/api/auth/resend-otp", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ mobileNumber })
    });

    const resData = await response.json();

    if (!resData.success) {

      showBanner("error", "⚠️  " + (resData.message || "Failed to resend OTP"));
      resendBtn.disabled = false;
      return;
    }

    showBanner("info", "ℹ️  New OTP generated. Check server terminal.");
    clearInterval(currentInterval);
    currentInterval = startTimer();

  } catch (err) {
    console.error(err);
    showBanner("error", "⚠️  Server error! Make sure backend is running.");
    resendBtn.disabled = false;
  } finally {
    resendBtn.textContent = "Resend OTP";
  }
});

document.getElementById("otp").addEventListener("input", () => {
  clearErrors();
});

function showFieldError(fieldId, errId, message) {
  document.getElementById(fieldId).classList.add("is-invalid");
  const err = document.getElementById(errId);
  err.textContent = message;
  err.classList.add("show");
}

function clearErrors() {
  document.getElementById("otp").classList.remove("is-invalid");
  const err = document.getElementById("otpErr");
  err.classList.remove("show");
  err.textContent = "";
  const banner = document.getElementById("alertBanner");
  banner.className   = "alert-banner";
  banner.textContent = "";
}

function showBanner(type, message) {
  const banner = document.getElementById("alertBanner");
  banner.className   = "alert-banner " + type;
  banner.textContent = message;
}