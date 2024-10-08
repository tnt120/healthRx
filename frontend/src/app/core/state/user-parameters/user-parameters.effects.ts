import { inject, Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ParametersService } from "../../services/parameters/parameters.service";
import { userParametersActions } from "./user-parameters.actions";
import { catchError, map, mergeMap, of } from "rxjs";
import { CustomSnackbarService } from "../../services/custom-snackbar/custom-snackbar.service";

@Injectable()
export class UserParametersEffects {
  private actions$ = inject(Actions);
  private parameterService = inject(ParametersService);
  private customSnackbarService = inject(CustomSnackbarService);

  setUserParametersMonitor$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(userParametersActions.set),
      mergeMap((action) => {
        return this.parameterService.setUserParametersMonitor(action.request).pipe(
          map(userParameters => userParametersActions.setSuccess({ userParameters })),
          catchError(err => {
            console.error('Error setting user parameters', err);
            this.customSnackbarService.openCustomSnackbar({ title: 'Niepowodzenie', message: 'Wystąpił błąd podczas zapisu parametrów', type: 'error', duration: 5000 });

            return of(userParametersActions.setError({ error: err }))
          })
        )
      })
    )
  });

  editUserParametersMonitor$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(userParametersActions.edit),
      mergeMap((action) => {
        return this.parameterService.editUserParametersMonitor(action.request).pipe(
          map(userParameter => userParametersActions.editSuccess({ userParameter })),
          catchError(err => {
            console.error('Error editing user parameters', err);
            this.customSnackbarService.openCustomSnackbar({ title: 'Niepowodzenie', message: 'Wystąpił błąd podczas edycji parametru', type: 'error', duration: 5000 });

            return of(userParametersActions.editError({ error: err }))
          })
        )
      })
    )
  });
}
