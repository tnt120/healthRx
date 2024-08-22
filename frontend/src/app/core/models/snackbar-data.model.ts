export interface SnackBarData {
  title: string;
  message: string;
  type: 'success' | 'error' | 'warning';
  duration: number;
}
