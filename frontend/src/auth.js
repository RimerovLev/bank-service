const STORAGE_KEY = "bank-service-auth";

export function saveAuth(auth) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(auth));
}

export function loadAuth() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function clearAuth() {
  localStorage.removeItem(STORAGE_KEY);
}

export function buildBasicAuthHeader(username, password) {
  const token = btoa(`${username}:${password}`);
  return `Basic ${token}`;
}
