import { Activity } from "./activity.model";

export interface ActivityResponse {
  mostPopularActivities: Activity[];
  otherActivities: Activity[];
}
