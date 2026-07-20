(() => {
  const TOKEN_KEY = "notes_api_token";
  const USER_KEY = "notes_api_user";

  const state = {
    token: localStorage.getItem(TOKEN_KEY),
    user: JSON.parse(localStorage.getItem(USER_KEY) || "null"),
    page: 0,
    size: 10,
    sortBy: "updatedAt",
    search: "",
    totalPages: 0,
    selectedId: null,
  };

  const el = {
    alert: document.getElementById("alert"),
    authView: document.getElementById("auth-view"),
    notesView: document.getElementById("notes-view"),
    sessionBar: document.getElementById("session-bar"),
    sessionUser: document.getElementById("session-user"),
    logoutBtn: document.getElementById("logout-btn"),
    loginForm: document.getElementById("login-form"),
    registerForm: document.getElementById("register-form"),
    searchForm: document.getElementById("search-form"),
    searchInput: document.getElementById("search-input"),
    sortBy: document.getElementById("sort-by"),
    pageSize: document.getElementById("page-size"),
    notesList: document.getElementById("notes-list"),
    prevPage: document.getElementById("prev-page"),
    nextPage: document.getElementById("next-page"),
    pageInfo: document.getElementById("page-info"),
    newNoteBtn: document.getElementById("new-note-btn"),
    emptyEditor: document.getElementById("empty-editor"),
    noteForm: document.getElementById("note-form"),
    noteId: document.getElementById("note-id"),
    noteTitle: document.getElementById("note-title"),
    noteContent: document.getElementById("note-content"),
    noteMeta: document.getElementById("note-meta"),
    deleteNoteBtn: document.getElementById("delete-note-btn"),
    saveNoteBtn: document.getElementById("save-note-btn"),
  };

  function showAlert(message, type = "info") {
    el.alert.textContent = message;
    el.alert.className = `alert ${type}`;
  }

  function clearAlert() {
    el.alert.className = "alert hidden";
    el.alert.textContent = "";
  }

  function setSession(token, user) {
    state.token = token;
    state.user = user;
    if (token) {
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }
    renderShell();
  }

  function renderShell() {
    const loggedIn = Boolean(state.token);
    el.authView.classList.toggle("hidden", loggedIn);
    el.notesView.classList.toggle("hidden", !loggedIn);
    el.sessionBar.classList.toggle("hidden", !loggedIn);
    if (loggedIn && state.user) {
      el.sessionUser.textContent = `${state.user.username} · ${state.user.email}`;
    }
  }

  async function api(path, options = {}) {
    const headers = {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    };
    if (state.token) {
      headers.Authorization = `Bearer ${state.token}`;
    }

    const response = await fetch(path, { ...options, headers });

    if (response.status === 204) {
      return null;
    }

    const text = await response.text();
    let body = null;
    if (text) {
      try {
        body = JSON.parse(text);
      } catch {
        body = { message: text };
      }
    }

    if (!response.ok) {
      if (response.status === 401 && state.token && path.startsWith("/api/notes")) {
        setSession(null, null);
        showAlert("Session expired. Please log in again.", "error");
      }
      const message = body?.message || `Request failed (${response.status})`;
      throw new Error(message);
    }

    return body;
  }

  function formatDate(value) {
    if (!value) return "";
    return new Date(value).toLocaleString();
  }

  function openEditor(note) {
    el.emptyEditor.classList.add("hidden");
    el.noteForm.classList.remove("hidden");
    el.noteId.value = note?.id ?? "";
    el.noteTitle.value = note?.title ?? "";
    el.noteContent.value = note?.content ?? "";
    el.deleteNoteBtn.classList.toggle("hidden", !note?.id);
    el.saveNoteBtn.textContent = note?.id ? "Update note" : "Create note";
    el.noteMeta.textContent = note?.id
      ? `ID ${note.id} · Created ${formatDate(note.createdAt)} · Updated ${formatDate(note.updatedAt)}`
      : "New note — not saved yet";
    state.selectedId = note?.id ?? null;
    highlightSelected();
  }

  function closeEditor() {
    el.noteForm.classList.add("hidden");
    el.emptyEditor.classList.remove("hidden");
    el.noteId.value = "";
    state.selectedId = null;
    highlightSelected();
  }

  function highlightSelected() {
    el.notesList.querySelectorAll(".note-item").forEach((btn) => {
      btn.classList.toggle("active", Number(btn.dataset.id) === state.selectedId);
    });
  }

  function renderNotes(pageData) {
    const notes = pageData.content || [];
    state.totalPages = pageData.totalPages || 0;
    state.page = pageData.number ?? state.page;

    el.notesList.innerHTML = "";
    if (notes.length === 0) {
      const empty = document.createElement("li");
      empty.className = "meta";
      empty.style.padding = "0.75rem";
      empty.textContent = state.search
        ? "No notes match your search."
        : "No notes yet. Create your first one.";
      el.notesList.appendChild(empty);
    } else {
      notes.forEach((note) => {
        const li = document.createElement("li");
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "note-item";
        btn.dataset.id = note.id;
        btn.innerHTML = `<h3></h3><p></p>`;
        btn.querySelector("h3").textContent = note.title;
        btn.querySelector("p").textContent = note.content;
        btn.addEventListener("click", () => loadNote(note.id));
        li.appendChild(btn);
        el.notesList.appendChild(li);
      });
    }

    const pageLabel = state.totalPages === 0 ? 0 : state.page + 1;
    el.pageInfo.textContent = `Page ${pageLabel} of ${state.totalPages || 0} · ${pageData.totalElements || 0} notes`;
    el.prevPage.disabled = state.page <= 0;
    el.nextPage.disabled = state.totalPages === 0 || state.page >= state.totalPages - 1;
    highlightSelected();
  }

  async function loadNotes() {
    const params = new URLSearchParams({
      page: String(state.page),
      size: String(state.size),
      sortBy: state.sortBy,
    });
    if (state.search.trim()) {
      params.set("search", state.search.trim());
    }
    const data = await api(`/api/notes?${params.toString()}`);
    renderNotes(data);
  }

  async function loadNote(id) {
    try {
      clearAlert();
      const note = await api(`/api/notes/${id}`);
      openEditor(note);
    } catch (err) {
      showAlert(err.message, "error");
    }
  }

  // Tabs
  document.querySelectorAll(".tab").forEach((tab) => {
    tab.addEventListener("click", () => {
      document.querySelectorAll(".tab").forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");
      const isLogin = tab.dataset.tab === "login";
      el.loginForm.classList.toggle("hidden", !isLogin);
      el.registerForm.classList.toggle("hidden", isLogin);
      clearAlert();
    });
  });

  el.registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearAlert();
    const form = new FormData(el.registerForm);
    try {
      await api("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({
          username: form.get("username"),
          email: form.get("email"),
          password: form.get("password"),
        }),
      });
      showAlert("Account created. Log in to continue.", "ok");
      document.querySelector('.tab[data-tab="login"]').click();
      el.loginForm.email.value = form.get("email");
      el.registerForm.reset();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearAlert();
    const form = new FormData(el.loginForm);
    try {
      const data = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: form.get("email"),
          password: form.get("password"),
        }),
      });
      setSession(data.token, { username: data.username, email: data.email });
      state.page = 0;
      closeEditor();
      await loadNotes();
      showAlert(`Welcome, ${data.username}.`, "ok");
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.logoutBtn.addEventListener("click", () => {
    setSession(null, null);
    closeEditor();
    el.notesList.innerHTML = "";
    showAlert("Logged out.", "info");
  });

  el.searchForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    state.search = el.searchInput.value;
    state.page = 0;
    try {
      clearAlert();
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.sortBy.addEventListener("change", async () => {
    state.sortBy = el.sortBy.value;
    state.page = 0;
    try {
      clearAlert();
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.pageSize.addEventListener("change", async () => {
    state.size = Number(el.pageSize.value);
    state.page = 0;
    try {
      clearAlert();
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.prevPage.addEventListener("click", async () => {
    if (state.page <= 0) return;
    state.page -= 1;
    try {
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.nextPage.addEventListener("click", async () => {
    if (state.page >= state.totalPages - 1) return;
    state.page += 1;
    try {
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.newNoteBtn.addEventListener("click", () => {
    clearAlert();
    openEditor(null);
    el.noteTitle.focus();
  });

  el.noteForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearAlert();
    const payload = {
      title: el.noteTitle.value.trim(),
      content: el.noteContent.value.trim(),
    };
    const id = el.noteId.value;
    try {
      const note = id
        ? await api(`/api/notes/${id}`, { method: "PUT", body: JSON.stringify(payload) })
        : await api("/api/notes", { method: "POST", body: JSON.stringify(payload) });
      showAlert(id ? "Note updated." : "Note created.", "ok");
      openEditor(note);
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  el.deleteNoteBtn.addEventListener("click", async () => {
    const id = el.noteId.value;
    if (!id) return;
    if (!confirm("Delete this note?")) return;
    try {
      await api(`/api/notes/${id}`, { method: "DELETE" });
      showAlert("Note deleted.", "ok");
      closeEditor();
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  async function boot() {
    renderShell();
    if (!state.token) return;
    try {
      await loadNotes();
    } catch (err) {
      showAlert(err.message, "error");
    }
  }

  boot();
})();
