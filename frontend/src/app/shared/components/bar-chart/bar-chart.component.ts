import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, ElementRef, inject, input, OnInit, ViewChild } from '@angular/core';
import { ChartResponse } from '../../../core/models/chart-response.model';
import { Chart, registerables } from 'chart.js';
import annotationPlugin from 'chartjs-plugin-annotation';
import { Days, getDayName } from '../../../core/enums/days.enum';

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.scss'
})
export class BarChartComponent implements OnInit {
  private readonly observer = inject(BreakpointObserver);

  @ViewChild('containerBody', { static: true }) containerBody!: ElementRef;

  chartData = input.required<ChartResponse>();

  labels = input.required<string[]>();

  chart: any;

  ngOnInit(): void {
    Chart.register(...registerables, annotationPlugin);
    this.generateChart();
  }

  generateChart(): void {
    const mainDataSet = this.chartData()?.data.map((d) => d.value > 0 ? d.value : 0);
    const additionalDataSet = this.chartData()?.data?.map((d) => d?.additionalValue ? d.additionalValue : 0);

    this.chart = new Chart('myChart', {
      type: 'bar',
      data: {
        labels: this.chartData()?.data.map((d) => getDayName(d.label as Days) ? getDayName(d.label as Days) : 'Całość'),
        datasets: [
          {
            label: this.labels()[0],
            data: mainDataSet,
            backgroundColor: '#1469C0',
            borderWidth: 0,
            borderRadius: 5,
          }, {
            label: this.labels()[1],
            data: additionalDataSet,
            backgroundColor: '#F05A7E',
            borderWidth: 0,
            borderRadius: 5,
          }
        ]
      },
      options: {
        maintainAspectRatio: false,
        responsive: true,
        onHover: (event: any, chartElement: any) => {
          event.native.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
        },
        scales: {
          y: {
            beginAtZero: false,
            type: 'linear',
            min: 0,
            max: 120,
            ticks: {
              callback: function(value) {
                if (typeof value === 'number' && value > 100) return '';
                return value;
              }
            }
          },
        }
      }
    });
  }
}
