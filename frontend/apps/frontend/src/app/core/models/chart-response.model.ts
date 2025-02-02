export interface ChartResponse {
  name: string;
  startDate: string;
  endDate: string;
  data: ChartData[];
}

export interface ChartData {
  label: string;
  value: number;
  additionalValue?: number;
}
