import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { ConvocationService } from '../../core/services/convocation.service';
import { PetitionService } from '../../core/services/petition.service';
import { ReportService } from '../../core/services/report.service';
import { ConvocationCategoryReport } from '../../core/models/report.model';
import { Convocation } from '../../core/models/convocation.model';
import { Petition } from '../../core/models/petition.model';
import { hasAnyRole } from '../../core/utils/helpers';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [StatusBadgeComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly reportService = inject(ReportService);
  private readonly convocationService = inject(ConvocationService);
  private readonly petitionService = inject(PetitionService);

  readonly user = this.authService.currentUser;
  readonly loading = signal(true);
  readonly reports = signal<ConvocationCategoryReport[]>([]);
  readonly convocations = signal<Convocation[]>([]);
  readonly petitions = signal<Petition[]>([]);

  get isAdminOrTeacher(): boolean {
    return hasAnyRole(this.user()?.roles ?? [], 'ADMIN', 'TEACHER');
  }

  get isStudent(): boolean {
    return hasAnyRole(this.user()?.roles ?? [], 'STUDENT');
  }

  ngOnInit(): void {
    if (this.isAdminOrTeacher) {
      this.loadAdminDashboard();
      return;
    }
    this.loadStudentDashboard();
  }

  private loadAdminDashboard(): void {
    this.reportService.getConvocationsByCategory().subscribe({
      next: (response) => {
        this.reports.set(response.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  private loadStudentDashboard(): void {
    this.convocationService.getAll(0, 5).subscribe({
      next: (convResponse) => {
        this.convocations.set(
          convResponse.data.content.filter((item) => item.state === 'PUBLICADA')
        );
      }
    });

    this.petitionService.getAll(0, 5).subscribe({
      next: (petitionResponse) => {
        this.petitions.set(petitionResponse.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
