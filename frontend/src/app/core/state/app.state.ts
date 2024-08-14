import { Activity } from "../models/activity.model";
import { City } from "../models/city.model";
import { Parameter } from "../models/parameter.model";
import { Specialization } from "../models/specialization.model";
import { UserParameterResponse } from "../models/user-parameter-response.model";
import { UserResponse } from "../models/user/user-response.model";

export interface AppState {
  user: UserResponse;
  activities: Activity[];
  cities: City[];
  parameters: Parameter[];
  specializations: Specialization[];
  userParameters: UserParameterResponse[];
}
