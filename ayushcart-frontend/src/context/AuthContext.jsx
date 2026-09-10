import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, getToken, setToken, setUnauthorizedHandler } from '../lib/api.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  // If a token is saved we must check it with the server before rendering protected pages
  const [loading, setLoading] = useState(() => Boolean(getToken()));

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(logout);
  }, [logout]);

  useEffect(() => {
    if (!getToken()) return;
    api('/auth/me')
      .then(setUser)
      .catch(() => logout())
      .finally(() => setLoading(false));
  }, [logout]);

  const value = useMemo(() => {
    const handleAuth = (data) => {
      setToken(data.token);
      setUser(data.user);
      return data.user;
    };
    return {
      user,
      loading,
      isAdmin: user?.role === 'ADMIN',
      login: (email, password) => api('/auth/login', { method: 'POST', body: { email, password } }).then(handleAuth),
      register: (fullName, email, password) =>
        api('/auth/register', { method: 'POST', body: { fullName, email, password } }).then(handleAuth),
      logout,
    };
  }, [user, loading, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
