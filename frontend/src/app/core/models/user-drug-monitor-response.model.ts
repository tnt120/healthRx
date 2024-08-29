import { Priority } from "../enums/priority.enum";
import { DrugResponse } from "./drug-response.model";

export interface UserDrugMonitorResponse {
  id: string;
  drug: DrugResponse;
  doseSize: number;
  priority: Priority;
  time: string;
  takenTime: string | null;
}
