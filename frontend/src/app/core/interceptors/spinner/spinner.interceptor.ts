import { HttpInterceptorFn } from '@angular/common/http';
import { SpinnerService } from '../../services/spinner/spinner.service';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';

export const spinnerInterceptor: HttpInterceptorFn = (req, next) => {
  const spinnerService = inject(SpinnerService);

  const excludeUrls = ['/api/parameters/users', 'api/drugs/user', 'api/drugs/monitor', 'api/drugs', '/api/doctor'];

  const shouldIngore = excludeUrls.some(url => req.url.includes(url));

  if (!shouldIngore) {
    spinnerService.loadingOn();
  }

  return next(req)
    .pipe(
      finalize(() => {
        if (!shouldIngore) {
          spinnerService.loadingOff();
        }
      })
    );
};
