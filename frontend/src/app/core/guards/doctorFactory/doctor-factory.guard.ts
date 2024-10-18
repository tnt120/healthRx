import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { map } from "rxjs";

export function doctorVerifyGuard(isVerified: boolean): CanActivateFn {
  return (route, state) => {
    const router = inject(Router);
    const store = inject(Store);

    return store.select('user').pipe(
      map((user) => {
        if (user.isDoctorVerified === isVerified) {
          return true;
        }

        if (isVerified) {
          return router.createUrlTree(['/doctor/unverified']);
        }

        return router.createUrlTree(['/doctor/patients']);
      })
    )
  };
}

export const doctorVerifiedGuard: CanActivateFn = doctorVerifyGuard(true);
export const doctorUnverifiedGuard: CanActivateFn = doctorVerifyGuard(false);
