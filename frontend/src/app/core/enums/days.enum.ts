export enum Days {
  MONDAY = 'Poniedziałek,Pon',
  TUESDAY = 'Wtorek,Wt',
  WEDNESDAY = 'Środa,Śr',
  THURSDAY = 'Czwartek,Czw',
  FRIDAY = 'Piątek,Pt',
  SATURDAY = 'Sobota,Sob',
  SUNDAY = 'Niedziela,Ndz'
}

export function getDayName(day: Days, short: boolean = false): string {
  const value = getEnumValue(day);
  const [fullName, shortName] = value.split(',');
  return short ? shortName : fullName;
}

function getEnumValue(day: string): string {
  switch (day) {
    case 'MONDAY':
      return Days.MONDAY;
    case 'TUESDAY':
      return Days.TUESDAY;
    case 'WEDNESDAY':
      return Days.WEDNESDAY;
    case 'THURSDAY':
      return Days.THURSDAY;
    case 'FRIDAY':
      return Days.FRIDAY;
    case 'SATURDAY':
      return Days.SATURDAY;
    case 'SUNDAY':
      return Days.SUNDAY;
    default: return '';
  }
}
