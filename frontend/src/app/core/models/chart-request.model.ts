export interface ChartRequest {
  dataId: string;
  type: 'PARAMETER' | 'DRUG' | 'ACTIVITY';
  startDate: string;
  endDate: string;
}
