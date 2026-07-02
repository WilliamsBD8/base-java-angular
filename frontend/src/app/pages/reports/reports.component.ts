import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { forkJoin } from 'rxjs';
import { ReportService } from '../../core/services/report.service';
import {
  ConvocationCategoryReport,
  PetitionConvocationReport,
  PetitionStateReport,
  ReportTab,
  ReportViewMode
} from '../../core/models/report.model';
import { getErrorMessage, showError } from '../../core/utils/alert';
import { ReportChartComponent } from '../../shared/components/report-chart/report-chart.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

interface ReportTabOption {
  id: ReportTab;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [ReportChartComponent, StatusBadgeComponent],
  templateUrl: './reports.component.html'
})
export class ReportsComponent implements OnInit {
  private readonly reportService = inject(ReportService);

  readonly loading = signal(true);
  readonly viewMode = signal<ReportViewMode>('table');
  readonly activeTab = signal<ReportTab>('categories');

  readonly categoryReports = signal<ConvocationCategoryReport[]>([]);
  readonly petitionConvocationReports = signal<PetitionConvocationReport[]>([]);
  readonly petitionStateReports = signal<PetitionStateReport[]>([]);

  readonly tabs: ReportTabOption[] = [
    { id: 'categories', label: 'Convocatorias por categoría', icon: 'ri-price-tag-3-line' },
    { id: 'petitions-convocations', label: 'Peticiones por convocatoria', icon: 'ri-calendar-event-line' },
    { id: 'petitions-states', label: 'Estados de peticiones', icon: 'ri-pie-chart-line' }
  ];

  readonly categoryChartConfig = computed<ChartConfiguration<'bar'>>(() => {
    const data = this.categoryReports();
    return {
      type: 'bar',
      data: {
        labels: data.map((item) => item.category),
        datasets: [
          {
            label: 'Borrador',
            data: data.map((item) => item.countDraft),
            backgroundColor: '#8592a3'
          },
          {
            label: 'Publicadas',
            data: data.map((item) => item.countPublished),
            backgroundColor: '#71dd37'
          },
          {
            label: 'Cerradas',
            data: data.map((item) => item.countClosed),
            backgroundColor: '#ff3e1d'
          }
        ]
      },
      options: this.stackedBarOptions('Categorías')
    };
  });

  readonly petitionConvocationChartConfig = computed<ChartConfiguration<'bar'>>(() => {
    const data = this.petitionConvocationReports();
    return {
      type: 'bar',
      data: {
        labels: data.map((item) => this.truncateLabel(item.convocationName)),
        datasets: [
          {
            label: 'Pendientes',
            data: data.map((item) => item.countPetitionsPending),
            backgroundColor: '#ffab00'
          },
          {
            label: 'Aprobadas',
            data: data.map((item) => item.countPetitionsAccepted),
            backgroundColor: '#71dd37'
          },
          {
            label: 'Rechazadas',
            data: data.map((item) => item.countPetitionsRejected),
            backgroundColor: '#ff3e1d'
          }
        ]
      },
      options: this.stackedBarOptions('Convocatorias')
    };
  });

  readonly petitionStateChartConfig = computed<ChartConfiguration<'doughnut'>>(() => {
    const data = this.petitionStateReports();
    return {
      type: 'doughnut',
      data: {
        labels: data.map((item) => this.stateLabel(item.state)),
        datasets: [
          {
            data: data.map((item) => item.countPetitions),
            backgroundColor: ['#ffab00', '#71dd37', '#ff3e1d'],
            borderWidth: 0
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    };
  });

  ngOnInit(): void {
    this.loadReports();
  }

  setTab(tab: ReportTab): void {
    this.activeTab.set(tab);
  }

  setViewMode(mode: ReportViewMode): void {
    this.viewMode.set(mode);
  }

  stateLabel(state: string): string {
    const labels: Record<string, string> = {
      PENDIENTE: 'Pendiente',
      APROBADA: 'Aprobada',
      RECHAZADA: 'Rechazada'
    };
    return labels[state] ?? state;
  }

  private loadReports(): void {
    this.loading.set(true);

    forkJoin({
      categories: this.reportService.getConvocationsByCategory(),
      petitionsConvocations: this.reportService.getPetitionsByConvocation(),
      petitionStates: this.reportService.getPetitionStates()
    }).subscribe({
      next: ({ categories, petitionsConvocations, petitionStates }) => {
        this.categoryReports.set(categories.data);
        this.petitionConvocationReports.set(petitionsConvocations.data);
        this.petitionStateReports.set(petitionStates.data);
        this.loading.set(false);
      },
      error: async (error) => {
        this.loading.set(false);
        await showError(getErrorMessage(error));
      }
    });
  }

  private truncateLabel(value: string, maxLength = 24): string {
    return value.length > maxLength ? `${value.slice(0, maxLength)}…` : value;
  }

  private stackedBarOptions(axisTitle: string) {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom' as const
        }
      },
      scales: {
        x: {
          stacked: true,
          title: {
            display: true,
            text: axisTitle
          }
        },
        y: {
          stacked: true,
          beginAtZero: true,
          ticks: {
            precision: 0
          },
          title: {
            display: true,
            text: 'Cantidad'
          }
        }
      }
    };
  }
}
