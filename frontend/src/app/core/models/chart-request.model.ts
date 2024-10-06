export interface ChartRequest {
  dataId: string | number;
  type: 'PARAMETER' | 'DRUG' | 'ACTIVITY';
  startDate: string;
  endDate: string;
}
