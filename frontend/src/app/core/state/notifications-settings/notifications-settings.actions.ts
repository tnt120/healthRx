import { createActionGroup, emptyProps, props } from "@ngrx/store";
import { NotificationsData } from "../../models/notifications-data.model";

export const notificationsSettingsActions = createActionGroup({
  source: 'notifications settings',
  events: {
    'edit': emptyProps(),
    'edit success': props<{notificationsSettings: NotificationsData}>(),
    'edit error': props<{error: any}>(),
  }
});
