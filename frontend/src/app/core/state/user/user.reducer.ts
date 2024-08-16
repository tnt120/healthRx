import { createReducer, on } from "@ngrx/store";
import { UserResponse } from "../../models/user/user-response.model";
import { configActions } from "../config/config.actions";
import { Roles } from "../../enums/roles.enum";

export const userInitialState: UserResponse = {
  firstName: "",
  role: Roles.NONE,
  email: "",
  pictureUrl: ""
};

export const userReducer = createReducer(
  userInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.user),
  on(configActions.logout, () => userInitialState)
);
