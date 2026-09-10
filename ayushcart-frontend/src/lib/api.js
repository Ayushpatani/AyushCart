// Small wrapper around fetch() that adds the JWT and turns API errors into exceptions.

const TOKEN_KEY = 'ayushcart.token';
const BASE_URL = import.meta.env.VITE_API_URL ?? '/api';

let unauthorizedHandler = null;

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

/** Called when a request with a token gets 401 (token expired) — AuthContext logs the user out. */
export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message ?? `Request failed (${status})`);
    this.status = status;
    this.fieldErrors = body?.fieldErrors ?? {};
  }
}

export async function api(path, { method = 'GET', body } = {}) {
  const headers = {};
  const token = getToken();
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;

  let res;
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, { message: 'Cannot reach the server. Check that the backend is running on port 8080.' });
  }

  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);

  if (!res.ok) {
    if (res.status === 401 && token && unauthorizedHandler) unauthorizedHandler();
    throw new ApiError(res.status, data);
  }
  return data;
}
