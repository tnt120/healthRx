import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { Specialization } from '../../models/specialization.model';

export const specializationsInitialState: Specialization[] = [];

export const specializationsReducer = createReducer(
  specializationsInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.specializations || []),
  on(configActions.logout, () => specializationsInitialState)
);
