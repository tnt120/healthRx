export interface UserActivityRequest {
  activityId?: string;
  duration: number;
  activityTime: Date;
  averageHeartRate: number | null;
  caloriesBurned: number | null;
}
