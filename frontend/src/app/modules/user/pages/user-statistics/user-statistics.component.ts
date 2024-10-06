import { map, Observable, Subscription, tap } from 'rxjs';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../core/services/statistics/statistics-service.service';
import { ChartResponse } from '../../../../core/models/chart-response.model';
import { ChartRequest } from '../../../../core/models/chart-request.model';
import { Parameter } from '../../../../core/models/parameter.model';
import { Store } from '@ngrx/store';
import { UserParameterResponse } from '../../../../core/models/user-parameter-response.model';
import { DatePipe } from '@angular/common';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';


@Component({
  selector: 'app-user-statistics',
  templateUrl: './user-statistics.component.html',
  styleUrl: './user-statistics.component.scss',
  providers: [DatePipe]
})
export class UserStatisticsComponent  {
  
}
