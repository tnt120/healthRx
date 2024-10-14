export interface DoctorVerificationRequest {
  validVerification: boolean;
  doctorId: string;
  message: string | null;
}
