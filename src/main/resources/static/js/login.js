document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const user = {
    username: document.getElementById("username").value.trim(),
    password: document.getElementById("password").value.trim()
  };

  // ===== FRONTEND VALIDATION =====
  if (!user.username) {
    alert("Username is required");
    return;
  }

  if (user.username.length < 3) {
    alert("Username must be at least 3 characters");
    return;
  }

  if (!user.password) {
    alert("Password is required");
    return;
  }

  if (user.password.length < 6) {
    alert("Password must be at least 6 characters");
    return;
  }

  // ===== SEND TO BACKEND =====
  try {
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user)
    });

    if (!response.ok) {
      alert("Invalid username or password");
      return;
    }

    const resData = await response.json();

    if (!resData.success) {
      alert(resData.message);
      return;
    }

    const loggedUser = resData.data;

    // OTP verification
    if (!loggedUser.verified) {
      localStorage.setItem("mobileNumber", loggedUser.mobileNumber);
      localStorage.setItem("usernameAfterOtp", loggedUser.username);
      window.location.href = "otp.html";
      return;
    }

    // Success → Dashboard
    localStorage.setItem("user", JSON.stringify(loggedUser));
    alert("Login Successfully");
    window.location.href = "dashboard.html";

  } catch (error) {
    console.error(error);
    alert("Server error. Make sure backend is running.");
  }
});
