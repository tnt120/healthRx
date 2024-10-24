export type NavItem = {
  icon: string;
  title: string;
  route: string;
  subItems?: NavItem[];
};

export const userHeaders: NavItem[] = [
  {
    icon: 'monitor_heart',
    title: 'Parametry',
    route: '/user/parameters'
  },
  {
    icon: 'pill',
    title: 'Apteczka',
    route: '/user/cabinet',
    subItems: [
      {
        icon: 'prescriptions',
        title: 'Moje leki',
        route: '/user/cabinet'
      },
      {
        icon: 'calendar_today',
        title: 'Kalendarz',
        route: '/user/cabinet/calendar'
      }
    ]
  },
  {
    icon: 'directions_walk',
    title: 'Aktywność',
    route: '/user/activities'
  },
  {
    icon: 'stethoscope',
    title: 'Lekarze',
    route: '/user/doctors',
    subItems: [
      {
        icon: 'search',
        title: 'Wyszukaj',
        route: '/user/doctors'
      },
      {
        icon: 'people',
        title: 'Moi lekarze',
        route: '/user/doctors/my'
      },
      {
        icon: 'mail',
        title: 'Czat',
        route: '/user/doctors/messages'
      }
    ]
  },
  {
    icon: 'bar_chart',
    title: 'Statystyki',
    route: '/user/statistics'
  }
];

export const unverifiedDoctorHeaders: NavItem[] = [
  {
    icon: 'policy',
    title: 'Weryfikacja',
    route: '/doctor/unverified'
  }
];

export const doctorHeaders: NavItem[] = [
  {
    icon: 'patient_list',
    title: 'Pacjenci',
    route: '/doctor/patients'
  },
  {
    icon: 'checklist',
    title: 'Zaproszenia',
    route: '/doctor/approvals'
  },
  {
    icon: 'mail',
    title: 'Wiadomości',
    route: '/doctor/messages'
  }
];

export const adminHeaders: NavItem[] = [
  {
    icon: 'dashboard',
    title: 'Dashboard',
    route: '/admin/dashboard'
  },
  {
    icon: 'description',
    title: 'Wnioski',
    route: '/admin/approvals',
  },
  {
    icon: 'monitor_heart',
    title: 'Parametry',
    route: '/admin/parameters-manage'
  }
];
