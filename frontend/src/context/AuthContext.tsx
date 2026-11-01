import React, { createContext, useContext, useState, useEffect } from 'react';
import type { User } from '../types/models';
import { authService } from '../api/waterServices';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  switchRole: (role: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('auth_token');
    const savedUser = localStorage.getItem('auth_user');

    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
      }
    } else {
      // Default initial session: auto-login as Admin for seamless educational evaluation
      login('admin', 'Password123!').catch(() => {
        // Fallback demo user if backend is offline
        const fallbackUser: User = {
          id: 1,
          username: 'admin',
          email: 'admin@veloia.water.sim',
          fullName: 'System Administrator [SIMULATED]',
          role: 'ADMIN',
          department: 'Executive IT & Operations',
          status: 'ACTIVE',
        };
        setUser(fallbackUser);
        setToken('simulated_demo_jwt_token');
      });
    }
    setIsLoading(false);
  }, []);

  const login = async (username: string, password: string): Promise<void> => {
    setIsLoading(true);
    try {
      const data = await authService.login(username, password);
      setToken(data.token);
      setUser(data.user);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = (): void => {
    authService.logout();
    setToken(null);
    setUser(null);
  };

  const switchRole = async (roleName: string): Promise<void> => {
    const username = roleName.toLowerCase();
    await login(username, 'Password123!');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        switchRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
