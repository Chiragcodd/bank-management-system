document.addEventListener("DOMContentLoaded", async () => {
  const user = JSON.parse(localStorage.getItem("user"));
  console.log("User from localStorage:", user);

  if (!user) {
    console.warn("User not logged in, redirecting to login.html");
    window.location.href = "login.html";
    return;
  }

  const baseUrl = "http://localhost:8080";

  document.getElementById("username").textContent = user.username || "N/A";
  document.getElementById("fullName").textContent = user.fullName || "N/A";

  async function loadAccount() {
    try {
      console.log(`Fetching account for userId=${user.id}`);
      const res = await fetch(`${baseUrl}/api/account/${user.id}`);
      console.log("Account fetch raw response:", res);
      const resData = await res.json();
      console.log("Account fetch response JSON:", resData);

      if (!resData.success || !resData.data) {
        alert("Failed to load account: " + (resData.message || "No data returned"));
        return;
      }

      const account = resData.data;
      if (!account.id) console.error("Account ID is undefined! This will break transaction loading.");

      document.getElementById("balance").textContent = (account.balance || 0).toFixed(2);
      document.getElementById("accountNumber").textContent = account.accountNumber || "N/A";
      localStorage.setItem("account", JSON.stringify(account));

      loadTransactions(account.id);

    } catch (err) {
      console.error("Error loading account:", err);
      alert("Server error while loading account. Check console.");
    }
  }

  async function loadTransactions(accountId) {
    if (!accountId) {
      console.error("Cannot load transactions: accountId undefined");
      return;
    }

    try {
      console.log(`Fetching transactions for accountId=${accountId}`);
      const res = await fetch(`${baseUrl}/api/transactions/${accountId}`);
      const resData = await res.json();
      console.log("Transactions fetch response:", resData);

      if (!resData.success || !resData.data) {
        alert("Failed to load transactions: " + (resData.message || "No data returned"));
        return;
      }

      const data = resData.data;
      const table = document.getElementById("transactionTable");
      table.innerHTML = "";

      const searchTerm = document.getElementById("searchTransaction").value.toLowerCase();

      data
        .filter(t => t.type?.toLowerCase().includes(searchTerm) || t.amount?.toString().includes(searchTerm))
        .forEach(t => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${t.dateTime ? new Date(t.dateTime).toLocaleString() : "N/A"}</td>
            <td>${t.type || "N/A"}</td>
            <td>₹${t.amount?.toFixed(2) || 0}</td>
          `;
          table.appendChild(row);
        });

    } catch (err) {
      console.error("Error loading transactions:", err);
      alert("Server error while loading transactions. Check console.");
    }
  }

  // Deposit
 document.getElementById("depositBtn").addEventListener("click", async () => {
  const amount = parseFloat(document.getElementById("depositAmount").value);
  if (!amount) return alert("Enter a valid amount");

  const user = JSON.parse(localStorage.getItem("user")); // Logged-in user

  const requestBody = {
    userId: user.id,
    amount: amount
  };

  try {
    console.log(`Depositing amount=${amount} for userId=${user.id}`);

    const res = await fetch(`${baseUrl}/api/account/deposit`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestBody)
    });

    const resData = await res.json();
    console.log("Deposit response:", resData);

    if (!resData.success) {
      alert("Deposit failed: " + (resData.message || "Unknown error"));
      return;
    }

    alert("Deposit successful!");
    document.getElementById("depositAmount").value = "";
    loadAccount(); // refresh balance

  } catch (err) {
    console.error("Deposit error:", err);
    alert("Server error during deposit.");
  }
});


 document.getElementById("withdrawBtn").addEventListener("click", async () => {
  const amount = parseFloat(document.getElementById("withdrawAmount").value);
  if (!amount) return alert("Enter a valid amount");

  const user = JSON.parse(localStorage.getItem("user")); // Logged-in user

  const requestBody = {
    userId: user.id,
    amount: amount
  };

  try {
    console.log(`Withdrawing amount=${amount} for userId=${user.id}`);

    const res = await fetch(`${baseUrl}/api/account/withdraw`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestBody)
    });

    const resData = await res.json();
    console.log("Withdraw response:", resData);

    if (!resData.success) {
      alert("Withdraw failed: " + (resData.message || "Unknown error"));
      return;
    }

    alert("Withdraw successful!");
    document.getElementById("withdrawAmount").value = "";
    loadAccount(); // refresh balance

  } catch (err) {
    console.error("Withdraw error:", err);
    alert("Server error during withdraw.");
  }
});


// Logout
  document.getElementById("logoutBtn").addEventListener("click", () => {
    if (confirm("Are you sure you want to logout?")) {
      localStorage.clear();
      window.location.href = "login.html";
    }
  });

// Copy account number
  document.getElementById("copyAccBtn").addEventListener("click", () => {
    const accNumber = document.getElementById("accountNumber").textContent;
    navigator.clipboard.writeText(accNumber);
    alert("Account number copied!");
  });

// Theme toggle
  const themeToggle = document.getElementById("themeToggle");
  themeToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark-theme");
    themeToggle.textContent = document.body.classList.contains("dark-theme") ? "☀️" : "🌙";
  });

// Search filter
  document.getElementById("searchTransaction").addEventListener("input", () => {
    const account = JSON.parse(localStorage.getItem("account"));
    if (account) loadTransactions(account.id);
  });

  loadAccount();
});