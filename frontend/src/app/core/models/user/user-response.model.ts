import { Roles } from "../../enums/roles.enum";

export interface UserResponse {
  firstName: string;
  role: Roles;
  email: string;
  pictureUrl: string;
}
