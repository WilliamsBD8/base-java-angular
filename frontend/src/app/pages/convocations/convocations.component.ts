import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ConvocationService } from '../../core/services/convocation.service';
import { AuthService } from '../../core/services/auth.service';
import { Convocation } from '../../core/models/convocation.model';
import { hasAnyRole } from '../../core/utils/helpers';
import { formatDateTime } from '../../core/utils/helpers';
import { confirmAction, getErrorMessage, showError, showSuccess } from '../../core/utils/alert';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-convocations',
  standalone: true,
  imports: [RouterLink, PaginationComponent, StatusBadgeComponent],
  templateUrl: './convocations.component.html'
})
export class ConvocationsComponent implements OnInit {
  private readonly convocationService = inject(ConvocationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly convocations = signal<Convocation[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly formatDateTime = formatDateTime;

  get canManage(): boolean {
    return hasAnyRole(this.authService.getRoles(), 'ADMIN', 'TEACHER');
  }

  get isStudent(): boolean {
    return hasAnyRole(this.authService.getRoles(), 'STUDENT');
  }

  ngOnInit(): void {
    this.loadConvocations();
  }

  loadConvocations(page = 0): void {
    this.loading.set(true);
    this.page.set(page);
    this.convocationService.getAll(page, 10).subscribe({
      next: (response) => {
        let content = response.data.content;
        if (this.isStudent) {
          content = content.filter((item) => item.state === 'PUBLICADA');
        }
        this.convocations.set(content);
        this.totalPages.set(response.data.totalPages);
        this.totalElements.set(response.data.totalElements);
        this.loading.set(false);
      },
      error: async (error) => {
        this.loading.set(false);
        await showError(getErrorMessage(error));
      }
    });
  }

  async publish(id: number): Promise<void> {
    const confirmed = await confirmAction('Publicar convocatoria', 'La convocatoria quedará visible para los estudiantes.');
    if (!confirmed) return;

    this.convocationService.publish(id).subscribe({
      next: async (response) => {
        await showSuccess(response.message);
        this.loadConvocations(this.page());
      },
      error: async (error) => await showError(getErrorMessage(error))
    });
  }

  async close(id: number): Promise<void> {
    const confirmed = await confirmAction('Cerrar convocatoria', 'No se podrán recibir más peticiones.');
    if (!confirmed) return;

    this.convocationService.close(id).subscribe({
      next: async (response) => {
        await showSuccess(response.message);
        this.loadConvocations(this.page());
      },
      error: async (error) => await showError(getErrorMessage(error))
    });
  }

  async remove(id: number): Promise<void> {
    const confirmed = await confirmAction('Eliminar convocatoria', 'Esta acción no se puede deshacer.');
    if (!confirmed) return;

    this.convocationService.delete(id).subscribe({
      next: async (response) => {
        await showSuccess(response.message);
        this.loadConvocations(this.page());
      },
      error: async (error) => await showError(getErrorMessage(error))
    });
  }

  applyToConvocation(convocationId: number): void {
    this.router.navigate(['/petitions'], { queryParams: { convocationId } });
  }
}
