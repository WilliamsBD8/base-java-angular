import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { PetitionService } from '../../core/services/petition.service';
import { ConvocationService } from '../../core/services/convocation.service';
import { AuthService } from '../../core/services/auth.service';
import { Petition, PetitionState } from '../../core/models/petition.model';
import { Convocation } from '../../core/models/convocation.model';
import { hasRole } from '../../core/utils/helpers';
import { formatDateTime } from '../../core/utils/helpers';
import { getErrorMessage, showError, showSuccess } from '../../core/utils/alert';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-petitions',
  standalone: true,
  imports: [ReactiveFormsModule, PaginationComponent, StatusBadgeComponent],
  templateUrl: './petitions.component.html'
})
export class PetitionsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly petitionService = inject(PetitionService);
  private readonly convocationService = inject(ConvocationService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  readonly petitions = signal<Petition[]>([]);
  readonly publishedConvocations = signal<Convocation[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly formatDateTime = formatDateTime;
  readonly states: PetitionState[] = ['PENDIENTE', 'APROBADA', 'RECHAZADA'];

  readonly createForm = this.fb.nonNullable.group({
    convocationId: [0]
  });

  get isAdmin(): boolean {
    return hasRole(this.authService.getRoles(), 'ADMIN');
  }

  get isStudent(): boolean {
    return hasRole(this.authService.getRoles(), 'STUDENT');
  }

  ngOnInit(): void {
    this.loadPetitions();
    if (this.isStudent) {
      this.loadPublishedConvocations();
    }
  }

  loadPetitions(page = 0): void {
    this.loading.set(true);
    this.page.set(page);
    this.petitionService.getAll(page, 10).subscribe({
      next: (response) => {
        this.petitions.set(response.data.content);
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

  private loadPublishedConvocations(): void {
    this.convocationService.getAll(0, 100).subscribe({
      next: (response) => {
        const published = response.data.content.filter((item) => item.state === 'PUBLICADA');
        this.publishedConvocations.set(published);

        const convocationId = Number(this.route.snapshot.queryParamMap.get('convocationId'));
        if (convocationId) {
          this.createForm.patchValue({ convocationId });
        } else if (published.length > 0) {
          this.createForm.patchValue({ convocationId: published[0].id });
        }
      }
    });
  }

  submitCreate(): void {
    const convocationId = this.createForm.controls.convocationId.value;
    if (!convocationId) {
      showError('Selecciona una convocatoria');
      return;
    }

    this.saving.set(true);
    this.petitionService.create({ convocationId }).subscribe({
      next: async (response) => {
        this.saving.set(false);
        await showSuccess(response.message);
        this.loadPetitions(this.page());
      },
      error: async (error) => {
        this.saving.set(false);
        await showError(getErrorMessage(error));
      }
    });
  }

  updateState(petitionId: number, state: PetitionState): void {
    this.petitionService.update(petitionId, { state }).subscribe({
      next: async (response) => {
        await showSuccess(response.message);
        this.loadPetitions(this.page());
      },
      error: async (error) => await showError(getErrorMessage(error))
    });
  }
}
