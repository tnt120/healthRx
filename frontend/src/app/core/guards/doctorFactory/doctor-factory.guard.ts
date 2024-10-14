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
          router.navigate(['/doctor/unverified']);
        } else {
          router.navigate(['/doctor/patients']);
        }
        return false;
      })
    )
  };
}

export const doctorVerifiedGuard: CanActivateFn = doctorVerifyGuard(true);
export const doctorUnverifiedGuard: CanActivateFn = doctorVerifyGuard(false);
