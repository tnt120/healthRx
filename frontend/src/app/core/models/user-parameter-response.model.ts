import { Parameter } from "./parameter.model";

export interface UserParameterResponse {
  id: string;
  parameter: Parameter;
  value: number | null;
}
