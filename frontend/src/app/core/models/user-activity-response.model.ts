import { Activity } from "./activity.model";

export interface UserActivityResponse {
  id: string;
  activity: Activity;
  activityTime: Date;
  duration: number;
  averageHeartRate: number | null;
  caloriesBurned: number | null;
}
