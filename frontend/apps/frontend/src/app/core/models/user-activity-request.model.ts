export interface UserActivityRequest {
  id?: string;
  activityId: string;
  duration: number;
  activityTime: Date;
  averageHeartRate: number | null;
  caloriesBurned: number | null;
}
