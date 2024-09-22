import { City } from "./city.model";
import { Specialization } from "./specialization.model";

export interface DoctorResponse {
  id: string;
  firstName: string;
  lastName: string;
  specializations: Specialization[];
  city: City;
  numberPWZ: string;
  pictureUrl: string;
}