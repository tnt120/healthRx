export interface GenerateReportRequest {
  startDate: string;
  endDate: string;
  userId: string;
  userDrugs: boolean;
  parametersStats: boolean;
  drugsStats: boolean;
  activitiesStats: boolean;
}
