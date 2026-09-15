const BASE_URL = "http://localhost:8080/api/admin";
const STORAGE_KEY = "admin_auth";

function getAuthHeader() {
  const stored = sessionStorage.getItem(STORAGE_KEY);
  return stored ? `Basic ${stored}` : null;
}

export function isLoggedIn() {
  return !!sessionStorage.getItem(STORAGE_KEY);
}

export function logout() {
  sessionStorage.removeItem(STORAGE_KEY);
}

async function request(path, options = {}) {
  const authHeader = getAuthHeader();
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(authHeader ? { Authorization: authHeader } : {}),
      ...(options.headers || {}),
    },
  });

  if (response.status === 401) {
    logout();
    const error = new Error("Требуется вход в систему");
    error.status = 401;
    throw error;
  }

  if (!response.ok) {
    let message = `Ошибка запроса (${response.status})`;
    try {
      const data = await response.json();
      message = data.message || message;
    } catch {
      // тело ответа не JSON — оставляем сообщение по умолчанию
    }
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) return null;
  return response.json();
}

// Проверяет логин/пароль, обращаясь к /me с Basic-заголовком, построенным
// из введённых значений (ещё не из sessionStorage).
export async function login(username, password) {
  const encoded = btoa(`${username}:${password}`);
  const response = await fetch(`${BASE_URL}/me`, {
    headers: { Authorization: `Basic ${encoded}` },
  });

  if (!response.ok) {
    throw new Error("Неверный логин или пароль");
  }

  sessionStorage.setItem(STORAGE_KEY, encoded);
  return response.json();
}

export const adminApi = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: "POST", body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: "PUT", body: JSON.stringify(body) }),
  del: (path) => request(path, { method: "DELETE" }),
};
