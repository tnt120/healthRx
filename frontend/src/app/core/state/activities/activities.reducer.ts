import { createReducer, on } from '@ngrx/store';
import { Activity } from '../../models/activity.model';
import { configActions } from '../config/config.actions';

export const activitiesInitialState: Activity[] = [];

export const activitiesReducer = createReducer(
  activitiesInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.activities || []),
  on(configActions.logout, () => activitiesInitialState)
);
