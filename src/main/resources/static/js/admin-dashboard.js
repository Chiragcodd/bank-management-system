document.addEventListener("DOMContentLoaded", async () => {

  const token   = localStorage.getItem("token");
  const user    = JSON.parse(localStorage.getItem("user"));
  const baseUrl = "http://localhost:8080";

  if (!user || !token || user.role !== "ADMIN") {
    window.location.href = "login.html";
    return;
  }

  async function loadAllAccounts(searchUsername = "") {
    try {
      const url = searchUsername
        ? `${baseUrl}/api/admin/search?username=${encodeURIComponent(searchUsername)}`
        : `${baseUrl}/api/admin/all-accounts`;

      const res = await fetch(url, {
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type":  "application/json"
        }
      });

      if (res.status === 403 || res.status === 401) {
        localStorage.clear();
        window.location.href = "login.html";
        return;
      }

      const resData  = await res.json();
      const accounts = resData.data || resData;
      const table    = document.getElementById("allAccountsTable");

      if (!accounts || accounts.length === 0) {
        table.innerHTML = `<tr><td colspan="8" class="text-center text-muted">No accounts found</td></tr>`;
        return;
      }

      table.innerHTML = accounts.map(acc => `
        <tr>
          <td>${acc.id}</td>
          <td>${acc.username}</td>
          <td>${acc.fullName}</td>
          <td>${acc.email}</td>
          <td>${acc.mobileNumber}</td>
          <td><span class="fw-bold">${acc.accountNumber}</span></td>
          <td class="text-success fw-bold">₹${acc.balance ? acc.balance.toFixed(2) : "0.00"}</td>
          <td><span class="badge bg-info">${acc.accountType}</span></td>
        </tr>
      `).join("");

    } catch (error) {
      console.error("Error loading accounts:", error);

      showToast("Server error while loading accounts.", "danger");
    }
  }

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

  document.getElementById("searchBtn").addEventListener("click", () => {
    loadAllAccounts(document.getElementById("searchUsername").value.trim());
  });

  document.getElementById("searchUsername").addEventListener("keydown", (e) => {
    if (e.key === "Enter") loadAllAccounts(e.target.value.trim());
  });

  document.getElementById("resetBtn").addEventListener("click", () => {
    document.getElementById("searchUsername").value = "";
    loadAllAccounts();
  });

  function showToast(message, type = "success") {
    const toastEl   = document.getElementById("liveToast");
    const toastBody = document.getElementById("toastBody");
    const colorMap  = { success: "bg-success", danger: "bg-danger", warning: "bg-warning", info: "bg-info" };
    toastEl.className    = `toast align-items-center text-white ${colorMap[type] || "bg-success"} border-0`;
    toastBody.textContent = message;
    new bootstrap.Toast(toastEl, { delay: 3500 }).show();
  }

  loadAllAccounts();
});