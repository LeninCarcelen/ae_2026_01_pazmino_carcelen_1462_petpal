import React from 'react';
import { Redirect } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export const RequireAuth: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Redirect to="/login" />;
  }

  return children;
};
