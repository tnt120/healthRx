import { DrugResponse } from "./drug-response.model";

export interface DrugStatisticsResponse {
  drug: DrugResponse;
  totalDosesTaken: number;
  totalDosesMissed: number;
  totalDaysTaken: number;
  totalDaysMissed: number;
  totalDaysPartiallyTaken: number;
  compliancePercentage: number;
  punctualityPercentage: number;
  avgDelay: number;
  firstLogDate: string;
  lastLogDate: string;
}
