import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { Parameter } from '../../models/parameter.model';

export const parametersInitialState: Parameter[] = [];

export const parametersReducer = createReducer(
  parametersInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.parameters || [])
);
