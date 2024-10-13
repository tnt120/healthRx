import { CanActivateFn, Router } from '@angular/router';
import { Roles } from '../../enums/roles.enum';
import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { map } from 'rxjs';

export function roleFactoryGuard(role: Roles): CanActivateFn {
  return (route, state) => {
    const router = inject(Router);
    const store = inject(Store);

    return store.select('user').pipe(
      map((user) => {;
        if (user.role !== role) {
          router.navigate(['/login']);
          return false;
        }
        return true;
      })
    )
  };
}

export const notAuthorizedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const store = inject(Store);

  return store.select('user').pipe(
    map((user) => {
      switch (user.role) {
        case Roles.HEAD_ADMIN:
        case Roles.ADMIN:
          router.navigate(['/admin']);
          return false;
        case Roles.USER:
          router.navigate(['/user']);
          return false;
        case Roles.DOCTOR:
          router.navigate(['/doctor']);
          return false;
        default:
          return true;
      }
    })
  )
};

export const userGuard: CanActivateFn = roleFactoryGuard(Roles.USER);
export const doctorGuard: CanActivateFn = roleFactoryGuard(Roles.DOCTOR);

export const adminOrHeadAdminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const store = inject(Store);

  return store.select('user').pipe(
    map((user) => {
      if (user.role === Roles.ADMIN || user.role === Roles.HEAD_ADMIN) {
        return true;
      }
      router.navigate(['/login']);
      return false;
    })
  )
};
