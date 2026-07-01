import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `<span class="badge" [class]="badgeClass">{{ label }}</span>`
})
export class StatusBadgeComponent {
  @Input() value = '';

  get label(): string {
    const labels: Record<string, string> = {
      BORRADOR: 'Borrador',
      PUBLICADA: 'Publicada',
      CERRADA: 'Cerrada',
      PENDIENTE: 'Pendiente',
      APROBADA: 'Aprobada',
      RECHAZADA: 'Rechazada'
    };
    return labels[this.value] ?? this.value;
  }

  get badgeClass(): string {
    const classes: Record<string, string> = {
      BORRADOR: 'bg-label-secondary',
      PUBLICADA: 'bg-label-success',
      CERRADA: 'bg-label-danger',
      PENDIENTE: 'bg-label-warning',
      APROBADA: 'bg-label-success',
      RECHAZADA: 'bg-label-danger'
    };
    return classes[this.value] ?? 'bg-label-primary';
  }
}
