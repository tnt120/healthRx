import { Days } from "../enums/days.enum";

export interface UserDrugMonitorRequest {
  id: string;
  drugId: number;
  day: string;
  time: string;
  takenTime: string | null;
}
