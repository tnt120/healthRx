import { City } from "../city.model";
import { Parameter } from "../parameter.model";
import { Specialization } from "../specialization.model";

export interface UserVerificationRequest {
  email: string;
  firstName: string;
  lastName: string;
  sex: string;
  birthDate: string;
  phoneNumber: string;

  parameters?: Parameter[];
  height?: number;
  parametersNotifications?: string | null;
  isBadResultsNotificationsEnabled?: boolean;
  isDrugNotificationsEnabled?: boolean;

  specializations?: Specialization[];
  city?: City;
  numberPWZ?: string;
  numberPESEL?: string;
  idPhotoFrontUrl?: string;
  idPhotoBackUrl?: string;
  profilePictureUrl?: string;
}
