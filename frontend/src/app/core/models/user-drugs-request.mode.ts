export interface UserDrugsRequest {
  drugId: number;
  doseSize: number;
  priority: string;
  startDate: string;
  endDate: string | null;
  amount: number | null;
  doseTimes: string[];
  doseDays: string[];
}
