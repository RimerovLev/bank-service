import { buildBasicAuthHeader } from "./auth.js";

const API_BASE = import.meta.env.VITE_API_BASE || "";

async function request(path, options = {}, auth) {
  const headers = new Headers(options.headers || {});
  headers.set("Content-Type", "application/json");

  if (auth?.username && auth?.password) {
    headers.set("Authorization", buildBasicAuthHeader(auth.username, auth.password));
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const isJson = res.headers.get("content-type")?.includes("application/json");
  const body = isJson ? await res.json() : null;

  if (!res.ok) {
    const message = body?.message || `${res.status} ${res.statusText}`;
    const details = body?.details || [];
    const error = new Error(message);
    error.status = res.status;
    error.details = details;
    throw error;
  }

  return body;
}

export const api = {
  register: (payload) => request("/account/register", { method: "POST", body: JSON.stringify(payload) }),
  login: (auth) => request("/account/login", { method: "POST" }, auth),
  getUserCards: (auth, page, size) => request(`/api/user/cards?page=${page}&size=${size}`, {}, auth),
  getUserCard: (auth, last4) => request(`/api/user/cards/${last4}`, {}, auth),
  getCardBalance: (auth, last4) => request(`/api/user/cards/${last4}/balance`, {}, auth),
  transfer: (auth, payload) => request("/api/user/cards/transfer", { method: "POST", body: JSON.stringify(payload) }, auth),
  requestBlock: (auth, last4) => request(`/api/user/cards/${last4}/request-block`, { method: "POST" }, auth),

  adminGetAllCards: (auth, page, size) => request(`/card/getAllCards?page=${page}&size=${size}`, {}, auth),
  adminFindCardsByOwner: (auth, ownerName, page, size) =>
    request(`/card/findCardsByName/${encodeURIComponent(ownerName)}?page=${page}&size=${size}`, {}, auth),
  adminCreateCard: (auth, payload) => request("/card/createNewCard", { method: "POST", body: JSON.stringify(payload) }, auth),
  adminActivateCard: (auth, payload) => request("/card/activate", { method: "POST", body: JSON.stringify(payload) }, auth),
  adminDeleteCard: (auth, ownerName, last4) =>
    request(`/card/${encodeURIComponent(ownerName)}/deleteCard/${encodeURIComponent(last4)}`, { method: "DELETE" }, auth),

  adminUpdateUser: (auth, login, payload) => request(`/account/user/${encodeURIComponent(login)}`, { method: "PUT", body: JSON.stringify(payload) }, auth),
  adminDeleteUser: (auth, login) => request(`/account/user/${encodeURIComponent(login)}`, { method: "DELETE" }, auth),
  adminAddRole: (auth, login, role) => request(`/account/user/${encodeURIComponent(login)}/role/${encodeURIComponent(role)}`, { method: "PUT" }, auth),
  adminRemoveRole: (auth, login, role) => request(`/account/user/${encodeURIComponent(login)}/role/${encodeURIComponent(role)}`, { method: "DELETE" }, auth)
};
