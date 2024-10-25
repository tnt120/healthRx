import { Roles } from "../../enums/roles.enum";

export interface UserSearchFilters {
  role: Roles | null;
  firstName: string | null;
  lastName: string | null;
}
