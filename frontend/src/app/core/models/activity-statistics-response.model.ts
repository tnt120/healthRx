import { Activity } from "./activity.model";

export interface ActivityStatisticsResponse {
  activity: Activity;
  minDuration: number;
  maxDuration: number;
  avgDuration: number;
  minHeartRate: number | null;
  maxHeartRate: number | null;
  avgHeartRate: number | null;
  minCaloriesBurned: number | null;
  maxCaloriesBurned: number | null;
  avgCaloriesBurned: number | null;
  logsCount: number;
  hoursCount: number;
  firstLogDate: string | null;
  lastLogDate: string | null;
}
