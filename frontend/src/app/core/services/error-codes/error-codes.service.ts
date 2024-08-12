import { Injectable } from '@angular/core';

type HttpStatus = 'NOT_IMPLEMENTED' | 'FORBIDDEN' | 'UNAUTHORIZED' | 'CONFLICT' | 'NOT_FOUND' | 'BAD_REQUEST';

interface ErrorCode {
  message: string;
  status: HttpStatus;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorCodesService {
  private readonly errorCodes: { [key: number]: ErrorCode } = {
    0: { message: 'Brak kodu', status: 'NOT_IMPLEMENTED' },
    100: { message: 'Nieprawidłowy token', status: 'FORBIDDEN' },
    300: { message: 'Token dostępu wygasł', status: 'UNAUTHORIZED' },
    301: { message: 'Link aktywacyjny stracił ważność, link z nowym kodem został wysłany na email', status: 'UNAUTHORIZED' },
    302: { message: 'Nieprawidłowy login lub hasło', status: 'UNAUTHORIZED' },
    303: { message: 'Użytkownik z tym adresem e-mail już istnieje', status: 'CONFLICT' },
    304: { message: 'Nieprawidłowy token odświeżający', status: 'FORBIDDEN' },
    305: { message: 'Podany link weryfikacyjny jest błędny. Poprawny link powinen znajdować się na twojej skrzynce email', status: 'FORBIDDEN' },
    306: { message: 'Konto z podanym adresem e-mail nie istnieje', status: 'FORBIDDEN' },
    307: { message: 'Konto nie zostało zweryfikowane. Zweryfikuj swoje konto aby móc się zalogować.', status: 'FORBIDDEN' },
    308: { message: 'Użytkownik nie znaleziony', status: 'NOT_FOUND' },
    309: { message: 'Podany parametr nie znaleziony', status: 'NOT_FOUND' },
    310: { message: 'Log parametrów już istnieje. Nieprawidłowa wiadomość', status: 'BAD_REQUEST' },
    311: { message: 'Log parametrów nie znaleziony', status: 'NOT_FOUND' },
    312: { message: 'Użytkownik próbuje zmodyfikować dane innej osoby', status: 'FORBIDDEN' },
    500: { message: 'Błąd serwera', status: 'NOT_IMPLEMENTED' }
  };

  getErrorMessage(code: number): string {
    const error = this.errorCodes[code];
    return error ? error.message : 'Wystąpił nieznany błąd';
  }
}
