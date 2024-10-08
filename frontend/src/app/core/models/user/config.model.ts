import { ActivityResponse } from "../activity-response.model";
import { City } from "../city.model";
import { NotificationsData } from "../notifications-data.model";
import { Parameter } from "../parameter.model";
import { Specialization } from "../specialization.model";
import { UserParameterResponse } from "../user-parameter-response.model";
import { UserResponse } from "./user-response.model";

export interface Config {
  user: UserResponse;
  parameters?: Parameter[];
  specializations?: Specialization[];
  activities?: ActivityResponse;
  cities?: City[];
  userParameters?: UserParameterResponse[];
  notificationsSettings?: NotificationsData;
}
