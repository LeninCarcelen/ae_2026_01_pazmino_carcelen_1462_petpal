import React from 'react';
import { Redirect } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { Role } from '../models/role.model';

/**
 * Uso: <RequireRole roles={[Role.ADMIN]}><UserListPage /></RequireRole>
 * Es un espejo del lado del cliente de lo que ya aplica SecurityConfig.kt
 * en el backend (que sigue siendo la autoridad real: aunque el guard falle
 * en ocultar un botón, el backend igual responde 403).
 */
export const RequireRole: React.FC<{ roles: Role[]; children: React.ReactElement }> = ({
  roles,
  children
}) => {
  const { hasRole } = useAuth();

  if (roles.length === 0 || hasRole(...roles)) {
    return children;
  }

  return <Redirect to="/tabs/profile" />;
};
