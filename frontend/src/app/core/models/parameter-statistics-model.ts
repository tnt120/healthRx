import { TrendType } from "../enums/trend-type.enum";
import { Parameter } from "./parameter.model";

export interface ParameterStatisticsResponse {
  parameter: Parameter;
  avgValue: number;
  minValue: number;
  maxValue: number;
  missedDays: number;
  longestBreak: number;
  logsCount: number;
  daysAboveMaxValue: number;
  daysBelowMinValue: number;
  firstLogDate: string;
  lastLogDate: string;
  trend: TrendType;
}
