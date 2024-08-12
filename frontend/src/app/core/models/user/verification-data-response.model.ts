import { Roles } from "../../enums/roles.enum";
import { City } from "../city.model";
import { Parameter } from "../parameter.model";
import { Specialization } from "../specialization.model";

export interface VerificationDataResponse {
  userEmail: string;
  role: Roles;
  parameters: Parameter[];
  specializations: Specialization[];
  cities: City[];
}
