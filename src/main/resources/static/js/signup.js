document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const user = {
    fullName: document.getElementById("fullName").value.trim(),
    email: document.getElementById("email").value.trim(),
    username: document.getElementById("username").value.trim(),
    password: document.getElementById("password").value.trim(),
    mobileNumber: document.getElementById("mobileNumber").value.trim(),
    accountType: document.getElementById("accountType").value
  };

  // ===== FRONTEND VALIDATION =====
  if(user.username.length < 3){
    alert("Username must be at least 3 characters");
    return;
  }

  if(user.password.length < 6){
    alert("Password must be at least 6 characters");
    return;
  }

  // Strong password optional
  if(!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/.test(user.password)){
    alert("Password must contain uppercase, lowercase, number, and special character");
    return;
  }

  if(!/^\S+@\S+\.\S+$/.test(user.email)){
    alert("Invalid email format");
    return;
  }

  if(!/^\d{10}$/.test(user.mobileNumber)){
    alert("Mobile number must be 10 digits");
    return;
  }

  if(!user.accountType){
    alert("Please select account type");
    return;
  }

  // ===== SEND TO BACKEND =====
  try {
    const response = await fetch("http://localhost:8080/api/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
    });

    const resData = await response.json();

    if(!resData.success){
      alert("Signup failed: " + resData.message);
      return;
    }

    alert("Signup successful! OTP sent to your mobile. Please verify OTP.");

    // store for OTP page
    localStorage.setItem("mobileNumber", user.mobileNumber);
    localStorage.setItem("usernameAfterOtp", user.username);

    // redirect to OTP page
    window.location.href = "otp.html";

  } catch (error) {
    console.error(error);
    alert("Server error. Make sure backend is running.");
  }
});


