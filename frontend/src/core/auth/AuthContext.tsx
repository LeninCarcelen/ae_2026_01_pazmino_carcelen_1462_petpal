import React, { createContext, useContext, useSyncExternalStore } from 'react';
import { authStore } from './authStore';
import { Role } from '../models/role.model';

interface AuthContextValue {
  isAuthenticated: boolean;
  roles: Role[];
  isAdmin: boolean;
  hasRole: (...roles: Role[]) => boolean;
  getRoles: () => Role[];
  login: () => void;
  logout: () => void;
  handleCallback: (code: string) => Promise<void>;
  getAccessToken: () => string | null;
  getCognitoSub: () => string | null;
}

const AuthReactContext = createContext<AuthContextValue | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // useSyncExternalStore re-renderiza cada vez que authStore.notify() se dispara
  // (login/logout/handleCallback), igual que el BehaviorSubject de la versión Angular.
  useSyncExternalStore(
    (cb) => authStore.subscribe(cb),
    () => authStore.getAccessToken()
  );

  const value: AuthContextValue = {
    isAuthenticated: authStore.isAuthenticated(),
    roles: authStore.getRoles(),
    isAdmin: authStore.isAdmin(),
    hasRole: (...roles) => authStore.hasRole(...roles),
    getRoles: () => authStore.getRoles(),
    login: () => authStore.login(),
    logout: () => authStore.logout(),
    handleCallback: (code) => authStore.handleCallback(code),
    getAccessToken: () => authStore.getAccessToken(),
    getCognitoSub: () => authStore.getCognitoSub()
  };

  return <AuthReactContext.Provider value={value}>{children}</AuthReactContext.Provider>;
};

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthReactContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return ctx;
}
