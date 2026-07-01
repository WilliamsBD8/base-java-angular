import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { hasAnyRole } from '../utils/helpers';

export const roleGuard = (...roles: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const userRoles = authService.getRoles();

    if (hasAnyRole(userRoles, ...roles)) {
      return true;
    }

    return router.createUrlTree(['/dashboard']);
  };
};
