import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { api, setAuthToken, setOnUnauthorized } from '../services/api';

const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
  const [user,setUser] = useState(null);
  const [loading,setLoading]=useState(true);

  useEffect(()=> {
    setOnUnauthorized(()=> { logout(); });
    const t = localStorage.getItem('token');
    if (t) {
      setAuthToken(t);
      api.me().then(u=> setUser(u)).catch(()=> { localStorage.removeItem('token'); })
        .finally(()=> setLoading(false));
    } else setLoading(false);
  },[]);

  const login = useCallback(async (email,password)=> {
    const data = await api.login(email,password);
    localStorage.setItem('token', data.token);
    setAuthToken(data.token);
    const me = await api.me();
    setUser(me);
  },[]);

  const signup = useCallback(async (payload)=> {
    const data = await api.signup(payload);
    localStorage.setItem('token', data.token);
    setAuthToken(data.token);
    const me = await api.me();
    setUser(me);
  },[]);

  const logout = useCallback(()=> {
    localStorage.removeItem('token');
    setAuthToken(null);
    setUser(null);
  },[]);

  return <AuthCtx.Provider value={{ user, loading, login, signup, logout }}>{!loading && children}</AuthCtx.Provider>;
}

export function useAuth(){ return useContext(AuthCtx); }
