import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { Config } from "../../models/user/config.model";

export const configActions = createActionGroup({
  source: 'config',
  events: {
    'load': emptyProps(),
    'load success': props<{config: Config}>(),
    'load error': props<{error: any}>()
  }
});
