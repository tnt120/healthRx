export interface DoctorSearch {
  firstName: string | null;
  lastName: string | null;
  specialization: string | null;
  city: string | null;
}

export interface DoctorSearchData {
  firstName: string | null;
  lastName: string | null;
  specialization: [];
  city: string | null;
}