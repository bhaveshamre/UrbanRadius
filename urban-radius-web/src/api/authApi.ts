const TOKEN_KEY = 'urban-radius-token';

export interface LoginResponse {
  accessToken: string;
  expiresIn: number;
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const body = new URLSearchParams({
    client_id: 'urban-radius-api',
    grant_type: 'password',
    username,
    password,
    scope: 'openid',
  });

  const response = await fetch('/auth/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body,
  });

  if (!response.ok) {
    throw new Error('Invalid email or password');
  }

  const data = (await response.json()) as { access_token: string; expires_in: number };
  return {
    accessToken: data.access_token,
    expiresIn: data.expires_in,
  };
}

export function saveToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token);
}

export function loadToken(): string | null {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function clearToken(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}

export function parseEmailFromToken(token: string): string | null {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/'))) as {
      email?: string;
    };
    return decoded.email ?? null;
  } catch {
    return null;
  }
}
