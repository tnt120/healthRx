import { Roles } from "../../enums/roles.enum";
import { Sex } from "../../enums/sex.enum";

export interface UserResponse {
  firstName: string;
  lastName: string;
  email: string;
  sex: Sex;
  birthDate: string;
  phoneNumber: string;
  height?: number;
  role: Roles;
  pictureUrl: string;
}
