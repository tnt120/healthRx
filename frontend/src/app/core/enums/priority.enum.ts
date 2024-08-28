export enum Priority {
  LOW = 'Niski',
  HIGH = 'Wysoki'
}

export function getPriorityName(priority: string): string {
  switch (priority) {
    case 'LOW':
      return Priority.LOW;
    case 'HIGH':
      return Priority.HIGH;
    default: return '';
  }
}
