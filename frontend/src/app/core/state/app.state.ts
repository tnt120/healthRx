import { ActivityResponse } from "../models/activity-response.model";
import { City } from "../models/city.model";
import { NotificationsData } from "../models/notifications-data.model";
import { Parameter } from "../models/parameter.model";
import { Specialization } from "../models/specialization.model";
import { UserParameterResponse } from "../models/user-parameter-response.model";
import { UserResponse } from "../models/user/user-response.model";

export interface AppState {
  user: UserResponse;
  activities: ActivityResponse;
  cities: City[];
  parameters: Parameter[];
  specializations: Specialization[];
  userParameters: UserParameterResponse[];
  notificationsSettings: NotificationsData;
}
