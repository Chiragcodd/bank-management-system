document.addEventListener("DOMContentLoaded", async () => {

  const user    = JSON.parse(localStorage.getItem("user"));
  const token   = localStorage.getItem("token");
  const baseUrl = "http://localhost:8080";

  if (!user || !token || user.role !== "USER") {

    showToast("Access denied! Redirecting to login...", "danger");
    setTimeout(() => window.location.href = "login.html", 1500);
    return;
  }

  document.getElementById("username").textContent = user.username || "N/A";
  document.getElementById("fullName").textContent  = user.fullName || "N/A";

  async function loadAccount() {
    try {
      const res     = await fetch(`${baseUrl}/api/account/me`, {
        headers: { "Authorization": `Bearer ${token}` }
      });
      const resData = await res.json();

      if (!resData.success || !resData.data) {

        showToast("Failed to load account: " + (resData.message || "Unknown error"), "danger");
        return;
      }

      const account = resData.data;
      document.getElementById("balance").textContent       = (account.balance || 0).toFixed(2);
      document.getElementById("accountNumber").textContent = account.accountNumber || "N/A";
      localStorage.setItem("account", JSON.stringify(account));
      loadTransactions();

    } catch (err) {
      console.error("Error loading account:", err);

      showToast("Server error while loading account.", "danger");
    }
  }

  async function loadTransactions() {
    try {
      const res     = await fetch(`${baseUrl}/api/account/transactions`, {
        headers: { "Authorization": `Bearer ${token}` }
      });
      const resData = await res.json();

      if (!resData.success || !resData.data) {
        document.getElementById("transactionTable").innerHTML =
          `<tr><td colspan="5" class="text-center text-muted">No transactions yet</td></tr>`;
        return;
      }

      localStorage.setItem("lastTransactions", JSON.stringify(resData.data));
      renderTransactions(resData.data);

    } catch (err) {
      console.error("Error loading transactions:", err);
    }
  }

  function renderTransactions(data) {
    const table      = document.getElementById("transactionTable");
    const searchTerm = document.getElementById("searchTransaction").value.toLowerCase();

    const filtered = data.filter(t =>
      t.type?.toLowerCase().includes(searchTerm) ||
      t.amount?.toString().includes(searchTerm) ||
      t.counterpartyAccount?.toLowerCase().includes(searchTerm)
    );

    if (filtered.length === 0) {
      table.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-3">No transactions found</td></tr>`;
      return;
    }

    table.innerHTML = filtered.map(t => {
      let typeLabel, typeBadge, counterpartyHtml = "-";

      if (t.type === "DEPOSIT") {
        typeLabel = "⬇️ DEPOSIT";
        typeBadge = "bg-success";
      } else if (t.type === "WITHDRAW") {
        typeLabel = "⬆️ WITHDRAW";
        typeBadge = "bg-danger";
      } else if (t.type === "TRANSFER") {
        if (t.direction === "SENT") {
          typeLabel        = "🔴 TRANSFER SENT";
          typeBadge        = "bg-danger";
          counterpartyHtml = `<small class="text-muted">To:</small> <strong>${t.counterpartyAccount}</strong>`;
        } else if (t.direction === "RECEIVED") {
          typeLabel        = "🟢 TRANSFER RECEIVED";
          typeBadge        = "bg-success";
          counterpartyHtml = `<small class="text-muted">From:</small> <strong>${t.counterpartyAccount}</strong>`;
        } else {
          typeLabel = "🔄 TRANSFER";
          typeBadge = "bg-primary";
          if (t.counterpartyAccount)
            counterpartyHtml = `<strong>${t.counterpartyAccount}</strong>`;
        }
      }

      return `
        <tr>
          <td>${t.dateTime ? new Date(t.dateTime).toLocaleString() : "N/A"}</td>
          <td><span class="badge ${typeBadge}">${typeLabel}</span></td>
          <td class="fw-bold">₹${t.amount?.toFixed(2) || "0.00"}</td>
          <td>${counterpartyHtml}</td>
          <td><span class="badge ${t.status === "SUCCESS" ? "bg-success" : "bg-warning"}">${t.status || "N/A"}</span></td>
        </tr>
      `;
    }).join("");
  }

  const txPanel     = document.getElementById("txPanel");
  const txToggleBtn = document.getElementById("txToggleBtn");

  txToggleBtn.addEventListener("click", () => {
    const isVisible = txPanel.style.display === "block";
    if (isVisible) {
      txPanel.style.display = "none";
      txToggleBtn.classList.remove("active");
      txToggleBtn.textContent = "📋 Transaction History";
    } else {
      txPanel.style.display = "block";
      txToggleBtn.classList.add("active");
      txToggleBtn.textContent = "✖ Close History";
      loadTransactions();
    }
  });
  async function performTransaction(endpoint, amount) {

    if (!amount || amount <= 0) {
      showToast("Enter a valid amount", "warning");
      return;
    }

    try {
      const res = await fetch(`${baseUrl}/api/account/${endpoint}`, {
        method:  "POST",
        headers: {
          "Content-Type":  "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ amount })
      });

      const resData = await res.json();

      if (!resData.success) {

        showToast(resData.message || `${endpoint} failed`, "danger");
        return;
      }

      showToast(
        `${endpoint.charAt(0).toUpperCase() + endpoint.slice(1)} of ₹${amount.toFixed(2)} successful!`,
        "success"
      );
      document.getElementById(`${endpoint}Amount`).value = "";
      loadAccount();

    } catch (err) {
      console.error(err);

      showToast("Server error. Make sure backend is running.", "danger");
    }
  }

  document.getElementById("depositBtn").addEventListener("click", () => {
    performTransaction("deposit", parseFloat(document.getElementById("depositAmount").value));
  });

  document.getElementById("withdrawBtn").addEventListener("click", () => {
    performTransaction("withdraw", parseFloat(document.getElementById("withdrawAmount").value));
  });

  document.getElementById("transferBtn").addEventListener("click", async () => {
    const toAccountNumber = document.getElementById("transferAccount").value.trim();
    const amount          = parseFloat(document.getElementById("transferAmount").value);

    if (!toAccountNumber) { showToast("Enter receiver account number", "warning"); return; }
    if (!amount || amount <= 0) { showToast("Enter a valid amount", "warning"); return; }

    try {
      const res = await fetch(`${baseUrl}/api/account/transfer`, {
        method:  "POST",
        headers: {
          "Content-Type":  "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ toAccountNumber, amount })
      });

      const resData = await res.json();

      if (!resData.success) {

        showToast(resData.message || "Transfer failed", "danger");
        return;
      }

      showToast(`₹${amount.toFixed(2)} transferred to ${toAccountNumber} successfully!`, "success");
      document.getElementById("transferAccount").value = "";
      document.getElementById("transferAmount").value  = "";
      loadAccount();

    } catch (err) {
      console.error(err);
      showToast("Server error during transfer.", "danger");
    }
  });

  const logoutModal      = document.getElementById("logoutModal");
  const cancelLogoutBtn  = document.getElementById("cancelLogoutBtn");
  const confirmLogoutBtn = document.getElementById("confirmLogoutBtn");

  document.getElementById("logoutBtn").addEventListener("click", () => {
    logoutModal.classList.add("show");
  });
  cancelLogoutBtn.addEventListener("click", () => {
    logoutModal.classList.remove("show");
  });
  logoutModal.addEventListener("click", (e) => {
    if (e.target === logoutModal) logoutModal.classList.remove("show");
  });
  confirmLogoutBtn.addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "login.html";
  });

  document.getElementById("copyAccBtn").addEventListener("click", () => {
    navigator.clipboard.writeText(document.getElementById("accountNumber").textContent);
    showToast("Account number copied!", "info");
  });

  document.getElementById("themeToggle").addEventListener("click", () => {
    document.body.classList.toggle("dark-theme");
    document.getElementById("themeToggle").textContent =
      document.body.classList.contains("dark-theme") ? "☀️" : "🌙";
  });

  document.getElementById("searchTransaction").addEventListener("input", () => {
    const stored = localStorage.getItem("lastTransactions");
    if (stored) renderTransactions(JSON.parse(stored));
  });

  function showToast(message, type = "success") {
    const toastEl   = document.getElementById("liveToast");
    const toastBody = document.getElementById("toastBody");

    const colorMap = {
      success: "bg-success",
      danger:  "bg-danger",
      warning: "bg-warning",
      info:    "bg-info"
    };

    toastEl.className   = `toast align-items-center text-white ${colorMap[type] || "bg-success"} border-0`;
    toastBody.textContent = message;

    const toast = new bootstrap.Toast(toastEl, { delay: 3500 });
    toast.show();
  }

  loadAccount();
});