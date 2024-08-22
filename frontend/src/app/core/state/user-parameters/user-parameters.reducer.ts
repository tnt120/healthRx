import { createReducer, on } from '@ngrx/store';
import { configActions } from '../config/config.actions';
import { UserParameterResponse } from '../../models/user-parameter-response.model';
import { userParametersActions } from './user-parameters.actions';

export const userParametersInitialState: UserParameterResponse[] = [];

export const userParametersReducer = createReducer(
  userParametersInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.userParameters || []),
  on(configActions.logout, () => userParametersInitialState),
  on(userParametersActions.getSuccess, (state, { userParameters }) => userParameters || []),
  on(userParametersActions.setSuccess, (state, { userParameters }) => (
    state.map(up => {
      const newUserParameter = userParameters.find(upr => upr.parameter.id === up.parameter.id);

      return newUserParameter ? newUserParameter : up;
    })
  )),
  on(userParametersActions.editSuccess, (state, { userParameter }) => (
    state.map(up => up.id === userParameter.id ? userParameter : up)
  )),
  on(userParametersActions.setError, (state, { error }) => {
    console.error('Error setting user parameters', error);

    return state;
  }),
  on(userParametersActions.editError, (state, { error }) => {
    console.error('Error editing user parameters', error);

    return state;
  })
);
