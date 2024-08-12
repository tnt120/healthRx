type SectionName = 'personalData' | 'userNotifications' | 'doctorPersonal';

type DataSection = {
  title: string;
  value: string;
};

export type UserSummary = {
  [key in SectionName]?: DataSection[];
};
