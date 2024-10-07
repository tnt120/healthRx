import { StatisticsType } from "../enums/statistics-type.enum";

export const Statistics_Type_Init = new Map<StatisticsType, boolean>([
  [StatisticsType.PARAMETER, false],
  [StatisticsType.DRUG, false],
]);
