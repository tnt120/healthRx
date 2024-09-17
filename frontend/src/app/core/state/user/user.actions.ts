import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { UserResponse } from "../../models/user/user-response.model";

export const userActions = createActionGroup({
  source: 'config',
  events: {
    'edit': emptyProps(),
    'edit success': props<{userData: UserResponse}>(),
    'edit error': props<{error: any}>(),
  }
});
