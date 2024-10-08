import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { ActivityResponse } from '../../models/activity-response.model';

export const activitiesInitialState: ActivityResponse = {
  mostPopularActivities: [],
  otherActivities: [],
};

export const activitiesReducer = createReducer(
  activitiesInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.activities || activitiesInitialState),
  on(configActions.logout, () => activitiesInitialState)
);
