import { UserParameterRequest } from './../../models/user-parameter-request.model';
import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { UserParameterResponse } from "../../models/user-parameter-response.model";

export const userParametersActions = createActionGroup({
  source: 'user parameters',
  events: {
    'set': props<{ request: UserParameterRequest[] }>(),
    'set success': props<{ userParameters: UserParameterResponse[] }>(),
    'set error': props<{ error: any }>(),

    'edit': props<{ request: UserParameterRequest[] }>(),
    'edit success': props<{ userParameters: UserParameterResponse[] }>(),
    'edit error': props<{ error: any }>(),
  }
})
