import { createReducer, on } from '@ngrx/store';
import { City } from './../../models/city.model';
import { configActions } from '../config/config.actions';

export const citiesInitialState: City[] = [];

export const citiesReducer = createReducer(
  citiesInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.cities || []),
  on(configActions.logout, () => citiesInitialState)
);
