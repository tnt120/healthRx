import { HttpInterceptorFn } from '@angular/common/http';
import { SpinnerService } from '../../services/spinner/spinner.service';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';

export const spinnerInterceptor: HttpInterceptorFn = (req, next) => {
  const spinnerService = inject(SpinnerService);

  const excludeUrls = [
    '/api/parameters/users',
    '/api/drugs/user',
    '/api/drugs/monitor',
    '/api/drugs',
    '/api/doctor',
    '/api/friendship/pending',
    '/api/friendship/rejected',
    '/api/friendship/accepted',
    '/api/statistics/parameters',
    '/api/statistics/drugs',
    '/api/statistics/chart',
    '/api/statistics/activities',
    '/api/admin/approvals',
  ];

  const excludeGetUrls = [
    '/api/activities/user',
    '/api/admin/parameters',
    '/api/admin/activities',
    '/api/admin/users',
    '/api/admin/dashboard',
  ];

  const shouldIngore = excludeUrls.some(url => req.url.includes(url)) ||
    (excludeGetUrls.some(url => req.url.includes(url)) && req.method === 'GET');;

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
