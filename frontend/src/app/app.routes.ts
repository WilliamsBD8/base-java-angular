import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'categories',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () => import('./pages/categories/categories.component').then((m) => m.CategoriesComponent)
      },
      {
        path: 'convocations',
        loadComponent: () =>
          import('./pages/convocations/convocations.component').then((m) => m.ConvocationsComponent)
      },
      {
        path: 'convocations/new',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () =>
          import('./pages/convocations/convocation-form/convocation-form.component').then(
            (m) => m.ConvocationFormComponent
          )
      },
      {
        path: 'convocations/:id/edit',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () =>
          import('./pages/convocations/convocation-form/convocation-form.component').then(
            (m) => m.ConvocationFormComponent
          )
      },
      {
        path: 'petitions',
        canActivate: [roleGuard('ADMIN', 'STUDENT')],
        loadComponent: () => import('./pages/petitions/petitions.component').then((m) => m.PetitionsComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
