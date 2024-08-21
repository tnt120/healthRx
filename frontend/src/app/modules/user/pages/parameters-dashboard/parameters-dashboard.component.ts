import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Parameter } from '../../../../core/models/parameter.model';
import { Observable, of, take, Subscription } from 'rxjs';
import { UserParameterResponse } from '../../../../core/models/user-parameter-response.model';
import { Store } from '@ngrx/store';
import { userParametersActions } from '../../../../core/state/user-parameters/user-parameters.actions';
import { UserParameterRequest } from '../../../../core/models/user-parameter-request.model';
import { Actions, ofType } from '@ngrx/effects';

@Component({
  selector: 'app-parameters-dashboard',
  templateUrl: './parameters-dashboard.component.html',
  styleUrl: './parameters-dashboard.component.scss'
})
export class ParametersDashboardComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly actions$ = inject(Actions);

  parameters$: Observable<Parameter[]>;

  userParameters$: Observable<UserParameterResponse[]>;

  matchingUserParameters: UserParameterResponse[] = [];

  parametersToSet: UserParameterResponse[] = [];

  settedParameters: UserParameterResponse[] = [];

  isFirstLoad = true;

  subscriptions: Subscription[] = [];

  constructor() {
    this.parameters$ = this.store.select('parameters');
    this.userParameters$ = this.store.select('userParameters');
  }

  ngOnInit(): void {
    this.matchingUserParameters = this.filterMatchingUserParameters();
    this.parametersToSet = [...this.matchingUserParameters.filter(param => param.value === null)];
    this.settedParameters = [...this.matchingUserParameters.filter(param => param.value !== null)];
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  filterMatchingUserParameters(): UserParameterResponse[] {
    let matchingParams: UserParameterResponse[] = [];

    this.subscriptions.push(this.userParameters$.subscribe(userParams => {
      this.subscriptions.push(this.parameters$.subscribe(params => {
        matchingParams = userParams
          .filter(userParam => params.some(param => param.id === userParam.parameter.id))
          .map(param => ({...param, value: param.value}));
      }));
    }));

    return matchingParams;
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
      console.log(res, this.parametersToSet, this.settedParameters);
    }));
  }

  reset() {
    this.parametersToSet = this.parametersToSet.map(param => ({...param, value: null}));
  }

  checkSaveButtonDisabled(): boolean {
    return !this.parametersToSet.some(param => param.value !== null);
  }
}
