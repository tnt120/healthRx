import { createReducer, on } from "@ngrx/store";
import { NotificationsData } from "../../models/notifications-data.model";
import { configActions } from "../config/config.actions";

export const initialNotificationsSettingsState: NotificationsData = {
  parametersNotifications: "",
  isBadResultsNotificationsEnabled: false,
  isDrugNotificationsEnabled: false
};

export const notificationsSettingsReducer = createReducer(
  initialNotificationsSettingsState,
  on(configActions.loadSuccess, (state, { config }) => config.notificationsSettings || initialNotificationsSettingsState),
  on(configActions.logout, () => initialNotificationsSettingsState)
);
