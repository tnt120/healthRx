import { Component, ElementRef, inject, input, OnInit, ViewChild } from '@angular/core';
import { ChartResponse } from '../../../core/models/chart-response.model';
import { Chart, registerables } from 'chart.js/auto';
import annotationPlugin from 'chartjs-plugin-annotation';
import { BreakpointObserver } from '@angular/cdk/layout';
import { combineLatest, min } from 'rxjs';
import { Parameter } from '../../../core/models/parameter.model';
import { ParametersService } from '../../../core/services/parameters/parameters.service';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.scss'
})
export class LineChartComponent implements OnInit {
  private readonly observer = inject(BreakpointObserver);

  private readonly parametersService = inject(ParametersService);

  @ViewChild('containerBody', { static: true }) containerBody!: ElementRef;

  chartData = input.required<ChartResponse & { selectedParameter: Parameter }>();

  userHeight = input.required<number>();

  chart: any;

  ngOnInit(): void {
    Chart.register(...registerables, annotationPlugin);
    this.generateChart();

    const phoneObserver = this.observer.observe('(max-width: 430px)');
    const smallScreenObserver = this.observer.observe('(max-width: 768px)');
    const mediumScreenObserver = this.observer.observe('(max-width: 1024px)');

    combineLatest([phoneObserver, smallScreenObserver, mediumScreenObserver]).subscribe(([phoneScreen, smallScreen, mediumScreen]) => {
      if (smallScreen.matches || mediumScreen.matches || phoneScreen.matches) {
        this.chart.resize();
        this.setChartWidth();
      }
    });
  }

  generateChart(): void {
    const dataPoints: number[] = this.chartData()?.data.map((d) => d.value) || [];

    const minStandardValue = this.chartData().selectedParameter.name === 'Waga' ?
      this.parametersService.getMaxWeightFromBmiAndHeight(this.userHeight(), this.chartData().selectedParameter.minStandardValue) :
      this.chartData().selectedParameter.minStandardValue;

    const minTemp = Math.min(...dataPoints, minStandardValue) - (this.chartData().selectedParameter.minStandardValue > 1000 ? 100 : 10);
    const minY = minTemp >= 0 ? minTemp : 0;

    const maxStandardValue = this.chartData().selectedParameter.name === 'Waga' ?
      this.parametersService.getMaxWeightFromBmiAndHeight(this.userHeight(), this.chartData().selectedParameter.maxStandardValue) :
      this.chartData().selectedParameter.maxStandardValue;

    const maxTemp = Math.max(...dataPoints, maxStandardValue) + (this.chartData().selectedParameter.maxStandardValue > 1000 ? 100 : 10)
    const maxY: number = maxTemp <= this.chartData().selectedParameter.maxValue ? maxTemp : this.chartData().selectedParameter.maxValue;

    this.chart = new Chart('lineChart', {
      type: 'line',
      data: {
        labels: this.chartData()?.data.map((d) => d.label.substring(0, 10)),
        datasets: [
          {
            label: this.chartData()?.name,
            data: dataPoints,
            backgroundColor: (context: any) => {
              const bgColor = [
                'rgba(20, 105, 192, 0.6)',
                'rgba(20, 105, 192, 0.15)',
              ];

              if (!context.chart.chartArea) {
                return;
              }

              const { ctx, data, chartArea: { top, bottom } } = context.chart;
              const gradientBg = ctx.createLinearGradient(0, top, 0, bottom);
              gradientBg.addColorStop(0, bgColor[0]);
              gradientBg.addColorStop(1, bgColor[1]);

              return gradientBg;
            },
            pointHoverBackgroundColor: 'rgba(20, 105, 192, 1)',
            borderColor: 'rgba(20, 105, 192, 1)',
            borderWidth: 1,
            pointRadius: 4,
            fill: true
          }
        ]
      },
      options: {
        maintainAspectRatio: false,
        responsive: true,
        animations: {
          tension: {
            duration: 2000,
            easing: 'linear',
            from: 0.4,
            to: 0,
            loop: true
          }
        },
        scales: {
          y: {
            beginAtZero: false,
            type: 'linear',
            min: minY,
            max: maxY,
          },
        },
        interaction: {
          mode: 'index',
          intersect: false,
        },
        plugins: {
          annotation: {
            annotations: {
              line1: {
                type: 'line',
                yMin: minStandardValue,
                yMax: minStandardValue,
                borderColor: 'red',
                borderWidth: 1,
              },
              line2: {
                type: 'line',
                yMin: maxStandardValue,
                yMax: maxStandardValue,
                borderColor: 'red',
                borderWidth: 1,
              }
            }
          },
          legend: {
            display: false
          }
        },
      }
    });

    this.setChartWidth();
  }

  setChartWidth(): void {
    const totalLabels = this.chart.data.labels.length;
    if (window.innerWidth >= 1024) {
      if (totalLabels > 150) {
        const newWidth = (totalLabels - 150) * 10;
        this.containerBody.nativeElement.style.width = `calc(100% + ${newWidth}px)`;
      }
    } else if (window.innerWidth >= 768) {
      if (totalLabels > 100) {
        const newWidth = (totalLabels - 100) * 15;
        this.containerBody.nativeElement.style.width = `calc(100% + ${newWidth}px)`;
      }
    } else {
      if (totalLabels > 50) {
        const newWidth = (totalLabels - 50) * 20;
        this.containerBody.nativeElement.style.width = `calc(100% + ${newWidth}px)`;
      }
    }
  }
}
