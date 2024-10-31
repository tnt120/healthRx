import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Parameter } from '../../../../core/models/parameter.model';
import { Observable, take, Subscription, map } from 'rxjs';
import { UserParameterResponse } from '../../../../core/models/user-parameter-response.model';
import { Store } from '@ngrx/store';
import { userParametersActions } from '../../../../core/state/user-parameters/user-parameters.actions';
import { UserParameterRequest } from '../../../../core/models/user-parameter-request.model';
import { Actions, ofType } from '@ngrx/effects';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { EditParameterMonitorDialogComponent, EditParameterMonitorDialogData } from '../../components/edit-parameter-monitor-dialog/edit-parameter-monitor-dialog.component';
import { CustomSnackbarService } from '../../../../core/services/custom-snackbar/custom-snackbar.service';
import { ParametersService } from '../../../../core/services/parameters/parameters.service';
import { Router } from '@angular/router';
import { UserResponse } from '../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-parameters-dashboard',
  templateUrl: './parameters-dashboard.component.html',
  styleUrl: './parameters-dashboard.component.scss'
})
export class ParametersDashboardComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly actions$ = inject(Actions);

  private readonly dialog = inject(MatDialog);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private readonly parameterService = inject(ParametersService);

  private readonly router = inject(Router);

  private readonly cdRef = inject(ChangeDetectorRef);

  userHeight$: Observable<number>;

  parameters$: Observable<Parameter[]>;

  userParameters$: Observable<UserParameterResponse[]>;

  parametersToSet: UserParameterResponse[] = [];

  settedParameters: UserParameterResponse[] = [];

  isFirstLoad = true;

  subscriptions: Subscription[] = [];

  protected isLoading$ = this.parameterService.getLoadingState();

  constructor() {
    this.parameters$ = this.store.select('parameters');
    this.userParameters$ = this.store.select('userParameters');
    this.userHeight$ = this.store.select('user').pipe(
      map((user: UserResponse) => user.height ?? 0)
    )
  }

  ngOnInit(): void {
    this.filterMatchingUserParameters();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  goToSettings() {
    this.router.navigate(['/user/settings'], { queryParams: { parametersChange: true } });
  }

  filterMatchingUserParameters(): void {
    this.subscriptions.push(
      this.parameterService.getUserParameters().subscribe(userParams => {
        this.store.dispatch(userParametersActions.getSuccess({ userParameters: userParams }));
        this.subscriptions.push(this.parameters$.subscribe(params => {
          const matchingParams: UserParameterResponse[] = userParams
            .filter(userParam => params.some(param => param.id === userParam.parameter.id))
            .map(param => ({...param, value: param.value}));

            this.parametersToSet = [...matchingParams.filter(param => param.value === null)];
            this.settedParameters = [...matchingParams.filter(param => param.value !== null)];
            this.isFirstLoad = false;
        }));
      })
    );
  }

  save() {
    const request: UserParameterRequest[] = this.parametersToSet.filter(param => param.value !== null).map(param => ({
      id: param.id,
      parameterId: param.parameter.id,
      value: param.value as number,
    }));

    this.store.dispatch(userParametersActions.set({ request }));

    this.subscriptions.push(this.actions$.pipe(
      ofType(userParametersActions.setSuccess),
      take(1)
    ).subscribe((res) => {
      this.parametersToSet = this.parametersToSet.filter(v => !res.userParameters.some(v1 => v1.id === v.id));
      this.settedParameters = [...this.settedParameters, ...res.userParameters];
      this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Wybrane parametry zostały zapisane', type: 'success', duration: 5000 });
      this.cdRef.detectChanges();
    }));
  }

  reset() {
    this.parametersToSet = this.parametersToSet.map(param => ({...param, value: null}));
  }

  checkSaveButtonDisabled(resetButton: boolean): boolean {
    const singleParamExist = !this.parametersToSet.some(param => param.value !== null);

    if (resetButton) {
      return singleParamExist;
    }

    const anyNegativeValue = this.parametersToSet.some(param => param.value && param.value <= 0);
    const anyPositiveValue = this.parametersToSet.some(param => param.value && param.value > 0);
    const anyValueAboveMax = this.parametersToSet.some(param => param.value && param.value > param.parameter.maxValue);
    const anyValueBelowMin = this.parametersToSet.some(param => param.value && param.value < param.parameter.minValue);

    return singleParamExist || anyNegativeValue || !anyPositiveValue || anyValueAboveMax || anyValueBelowMin;
  }

  getTooltipText(param: UserParameterResponse): string {
    if (param.parameter.name === 'Waga') return `Wartość prawidłowa BMI wynosi pomiędzy: ${param.parameter.minStandardValue} a ${param.parameter.maxStandardValue}`;
    return `Wartość prawidłowa wynosi pomiędzy: ${param.parameter.minStandardValue} a ${param.parameter.maxStandardValue}`;
  }

  editParameter(param: UserParameterResponse) {
    const data: EditParameterMonitorDialogData = { userParameter: param };

    const dialogRef: MatDialogRef<EditParameterMonitorDialogComponent, UserParameterResponse> = this.dialog.open(EditParameterMonitorDialogComponent, { data, width: '400px',  });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const request: UserParameterRequest = {
            id: result.id,
            parameterId: result.parameter.id,
            value: result.value as number,
          };

          this.store.dispatch(userParametersActions.edit({ request }));

          this.subscriptions.push(
            this.actions$.pipe(
              ofType(userParametersActions.editSuccess),
              take(1)
            ).subscribe((res) => {
              this.settedParameters = this.settedParameters.map(param => param.id === res.userParameter.id ? res.userParameter : param);
              this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Parametr został zaktualizowany', type: 'success', duration: 5000 });
              this.cdRef.detectChanges();
            })
          );
        }
      })
    )
  }

  getBmi(height: number, weight: number): number {
    return this.parameterService.calculateBmi(height, weight);
  }

  protected checkStandards(paramter: Parameter, value: number, height: number): boolean {
    let val = value;

    if (paramter.name === 'Waga') {
      val = this.getBmi(height, value);
    }
    return val! <= paramter.maxStandardValue && val! >= paramter.minStandardValue;
  }
}
