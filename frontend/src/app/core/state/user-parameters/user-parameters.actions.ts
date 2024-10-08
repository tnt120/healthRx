import { UserParameterRequest } from './../../models/user-parameter-request.model';
import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { UserParameterResponse } from "../../models/user-parameter-response.model";

export const userParametersActions = createActionGroup({
  source: 'user parameters',
  events: {
    'get': emptyProps(),
    'get success': props<{ userParameters: UserParameterResponse[] }>(),
    'get error': props<{ error: any }>(),

    'set': props<{ request: UserParameterRequest[] }>(),
    'set success': props<{ userParameters: UserParameterResponse[] }>(),
    'set error': props<{ error: any }>(),

    'edit': props<{ request: UserParameterRequest }>(),
    'edit success': props<{ userParameter: UserParameterResponse }>(),
    'edit error': props<{ error: any }>(),

    'update': emptyProps(),
    'update success': props<{ userParameters: UserParameterResponse[] }>(),
    'update error': props<{ error: any }>()
  }
})
