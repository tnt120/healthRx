import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, ElementRef, inject, input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
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

  labels = input<string[]>([]);

  chart: any;

  ngOnInit(): void {
    Chart.register(...registerables, annotationPlugin);
    this.generateChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chart) {
      this.chart.destroy();
      this.generateChart();
    }
  }

  generateChart(): void {
    let datasets: { label: string; data: number[]; backgroundColor: string; borderWidth: number; borderRadius: number; }[] = [];
    let min = 0;
    let max = 0;
    let labels: string[] = [];

    if (this.labels().length > 1) {
      const mainDataSet = this.chartData()?.data.map((d) => d.value > 0 ? d.value : 0);
      const additionalDataSet = this.chartData()?.data?.map((d) => d?.additionalValue ? d.additionalValue : 0);

      labels = this.chartData()?.data.map((d) => getDayName(d.label as Days) ? getDayName(d.label as Days) : 'Całość');

      max = 120;

      datasets = [{
        label: this.labels()![0],
        data: mainDataSet,
        backgroundColor: '#1469C0',
        borderWidth: 0,
        borderRadius: 5,
      }, {
        label: this.labels()![1],
        data: additionalDataSet,
        backgroundColor: '#F05A7E',
        borderWidth: 0,
        borderRadius: 5,
      }];
    } else {
      const backgrounds = Array(this.chartData()?.data.length).fill('#1469C0').map((color, index) => {
        const shade = Math.floor(200 - (index * 20));
        return `rgb(20, 105, ${shade})`;
      });

      datasets = this.chartData()?.data.map((d, index) => ({
        label: d.label,
        data: [d.value],
        backgroundColor: backgrounds[index],
        borderWidth: 0,
        borderRadius: 5,
      }));

      max = Math.max(...this.chartData()?.data.map(d => d.value)) + 2;

      labels = this.labels();
    }

    this.chart = new Chart('barChart', {
      type: 'bar',
      data: {
        labels: labels,
        datasets: datasets
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
            min: min,
            max: max,
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
