import { Parameter } from "../parameter.model";
import { Unit } from "../unit.model";

export interface AdminParameterResponse {
  parameters: Parameter[];
  units: Unit[];
}
