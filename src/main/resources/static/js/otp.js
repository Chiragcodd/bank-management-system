document.addEventListener("DOMContentLoaded", () => {
  const mobile = localStorage.getItem("mobileNumber");

  if (!mobile) {
    alert("Mobile number not found. Please login/signup again.");
    window.location.href = "login.html";
    return;
  }

  document.getElementById("mobileNumber").value = mobile;
});

document.getElementById("otpForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const mobile = document.getElementById("mobileNumber").value;
  const otp = document.getElementById("otp").value;
  const username = localStorage.getItem("usernameAfterOtp");

  try {
    const response = await fetch(
      `http://localhost:8080/api/auth/verify-otp?mobileNumber=${mobile}&otp=${otp}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      }
    );

    const data = await response.json();

    if (data.success) {
      alert("Mobile verified successfully!");
      localStorage.removeItem("mobileNumber");

// Auto login after OTP
      const loginResponse = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username, password: prompt("Enter your password to login after OTP") })
      });

      const loginData = await loginResponse.json();

      if(loginData.success){
        localStorage.setItem("user", JSON.stringify(loginData.data));
        localStorage.removeItem("usernameAfterOtp");
        window.location.href = "dashboard.html";
      } else {
        alert("Login failed after OTP: " + loginData.message);
        window.location.href = "login.html";
      }

    } else {
      alert(data.message || "Invalid OTP");
    }
  } catch (error) {
    console.error(error);
    alert("Server error. Make sure backend is running.");
  }
});


// ---------- TIMER LOGIC ----------
let timeLeft = 60;
const timer = document.getElementById("timer");
const resendBtn = document.getElementById("resendOtpBtn");

const interval = setInterval(() => {
  timeLeft--;
  timer.textContent = timeLeft;

  if (timeLeft <= 0) {
    clearInterval(interval);
    resendBtn.disabled = false; // Enable button after 60 sec
  }
}, 1000);

// ---------- RESEND OTP LOGIC ----------
document.getElementById("resendOtpBtn").addEventListener("click", async () => {
  const mobileNumber = localStorage.getItem("mobileNumber");

  if (!mobileNumber) {
    alert("Mobile number not found!");
    return;
  }

  try {
    const response = await fetch(
      "http://localhost:8080/api/auth/resend-otp?mobileNumber=" + mobileNumber,
      { method: "POST" }
    );

    const resData = await response.json();

    if (resData.success) {
      alert("New OTP sent! Check terminal.");

      // Reset timer again to 60 seconds
      timeLeft = 60;
      resendBtn.disabled = true;

      const newInterval = setInterval(() => {
        timeLeft--;
        timer.textContent = timeLeft;

        if (timeLeft <= 0) {
          clearInterval(newInterval);
          resendBtn.disabled = false;
        }
      }, 1000);

    } else {
      alert("Failed: " + resData.message);
    }
  } catch (error) {
    console.error(error);
    alert("Server error!");
  }
});
