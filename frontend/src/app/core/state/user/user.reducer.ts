import { createReducer, on } from "@ngrx/store";
import { UserResponse } from "../../models/user/user-response.model";
import { configActions } from "../config/config.actions";
import { Roles } from "../../enums/roles.enum";
import { Sex } from "../../enums/sex.enum";
import { userActions } from "./user.actions";

export const userInitialState: UserResponse = {
  id: "",
  firstName: "",
  lastName: "",
  email: "",
  sex: Sex.NONE,
  birthDate: "",
  phoneNumber: "",
  height: undefined,
  role: Roles.NONE,
  pictureUrl: ""
};

export const userReducer = createReducer(
  userInitialState,
  on(configActions.loadSuccess, (state, { config }) => config.user),
  on(configActions.logout, () => userInitialState),
  on(userActions.editSuccess, (state, { userData }) => userData),
);
