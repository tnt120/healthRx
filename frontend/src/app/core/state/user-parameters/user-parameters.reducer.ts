import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { UserParameterResponse } from '../../models/user-parameter-response.model';

export const userParametersInitialState: UserParameterResponse[] = [];

export const userParametersReducer = createReducer(
  userParametersInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.userParameters || [])
);
