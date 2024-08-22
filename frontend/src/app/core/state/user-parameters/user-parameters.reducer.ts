import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { UserParameterResponse } from '../../models/user-parameter-response.model';
import { userParametersActions } from './user-parameters.actions';

export const userParametersInitialState: UserParameterResponse[] = [];

export const userParametersReducer = createReducer(
  userParametersInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.userParameters || []),
  on(configActions.logout, () => userParametersInitialState),
  on(userParametersActions.setSuccess, (state, { userParameters }) => userParameters || []),
  on(userParametersActions.editSuccess, (state, { userParameter }) => [...state, userParameter] || []),
  on(userParametersActions.setError, (state, { error }) => {
    console.error('Error setting user parameters', error);

    return state;
  }),
  on(userParametersActions.editError, (state, { error }) => {
    console.error('Error editing user parameters', error);

    return state;
  })
);
