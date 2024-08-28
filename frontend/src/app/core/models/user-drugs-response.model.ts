import { Days } from "../enums/days.enum";
import { Priority } from "../enums/priority.enum";
import { DrugResponse } from "./drug-response.model";

export interface UserDrugsResponse {
  id: number;
  drug: DrugResponse;
  doseSize: number;
  priority: Priority;
  startDate: string;
  endDate: string;
  amount: number;
  doseTimes: string[];
  doseDays: Days[];
}
