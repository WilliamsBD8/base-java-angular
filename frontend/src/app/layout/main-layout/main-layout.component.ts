import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { hasAnyRole } from '../../core/utils/helpers';
import { confirmAction } from '../../core/utils/alert';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.component.html'
})
export class MainLayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly user = this.authService.currentUser;

  readonly menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'ri-dashboard-line', route: '/dashboard', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { label: 'Categorías', icon: 'ri-price-tag-3-line', route: '/categories', roles: ['ADMIN', 'TEACHER'] },
    { label: 'Convocatorias', icon: 'ri-calendar-event-line', route: '/convocations', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    { label: 'Peticiones', icon: 'ri-file-list-3-line', route: '/petitions', roles: ['ADMIN', 'STUDENT'] }
  ];

  visibleMenu(): MenuItem[] {
    const roles = this.user()?.roles ?? [];
    return this.menuItems.filter((item) => hasAnyRole(roles, ...item.roles));
  }

  async logout(): Promise<void> {
    const confirmed = await confirmAction('Cerrar sesión', '¿Deseas salir del sistema?');
    if (!confirmed) return;

    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}
