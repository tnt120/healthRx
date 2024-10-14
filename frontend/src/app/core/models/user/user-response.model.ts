import { Roles } from "../../enums/roles.enum";
import { Sex } from "../../enums/sex.enum";
import { UnverifiedDoctor } from "./unverified-doctor-model";

export interface UserResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  sex: Sex;
  birthDate: string;
  phoneNumber: string;
  height?: number;
  role: Roles;
  pictureUrl: string;
  isDoctorVerified?: boolean;
  unverifiedDoctor?: UnverifiedDoctor;
}
