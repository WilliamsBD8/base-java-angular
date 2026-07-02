import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  effect,
  input,
  viewChild
} from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-report-chart',
  standalone: true,
  template: `
    <div class="report-chart">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [
    `
      :host {
        display: block;
      }

      .report-chart {
        position: relative;
        height: 360px;
      }
    `
  ]
})
export class ReportChartComponent implements AfterViewInit, OnDestroy {
  readonly config = input.required<ChartConfiguration>();

  private readonly chartCanvas = viewChild.required<ElementRef<HTMLCanvasElement>>('chartCanvas');
  private chart?: Chart;
  private viewReady = false;

  constructor() {
    effect(() => {
      const configuration = this.config();
      if (this.viewReady) {
        this.renderChart(configuration);
      }
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.renderChart(this.config());
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  private renderChart(configuration: ChartConfiguration): void {
    this.chart?.destroy();
    this.chart = new Chart(this.chartCanvas().nativeElement, configuration);
  }
}
